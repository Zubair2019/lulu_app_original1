"""Run a uimatch flow with Portal as the screen reader.

uimatch's own `flow` command gives Portal 5 s to answer; on this phone it often needs longer, and the run then
falls back to uiautomator, which can't read Lulu's animated home page. This retries the connection until Portal
is in use, then hands the device to uimatch's run_flow (same safety checks, same capture folders).

Usage (from anywhere):
    uv run --quiet --project D:\\uimatch python D:\\lulu_app\\tools\\flow.py <flow-name> [key=value ...]
"""
import asyncio
import sys
from pathlib import Path

from mobilerun_core_local.driver.android import AndroidDriver
from uimatch import project
from uimatch.device import Device
from uimatch.flows import run_flow

PROJECT = Path(__file__).resolve().parents[1]


async def _patient_probe(self, portal) -> bool:
    """Same check as the driver's, but Portal gets 15 s and three tries (it is slow while Lulu animates)."""
    try:
        await portal.connect()
    except Exception:  # noqa: BLE001
        return False
    for _ in range(3):
        try:
            r = await asyncio.wait_for(portal.ping(), timeout=15.0)
            if isinstance(r, dict) and r.get("status") == "success":
                return True
        except Exception:  # noqa: BLE001
            await asyncio.sleep(1)
    return False


AndroidDriver._probe_portal = _patient_probe


async def portal_device(tries: int = 5) -> Device:
    for i in range(tries):
        d = await Device().connect()
        if d.portal:
            return d
        print(f"Portal didn't answer in time (try {i + 1}/{tries}); retrying")
        await asyncio.sleep(2)
    raise SystemExit("Portal is not answering; on the phone switch Mobilerun Portal off and on in Accessibility.")


async def main(name: str, params: dict) -> int:
    cfg = project.config(str(PROJECT))
    dev = await portal_device()
    out, m = await run_flow(project.flow_path(str(PROJECT), name), params=params, device=dev,
                            captures_root=project.root(str(PROJECT)) / "captures", redact=cfg.get("redact"))
    return 1 if m["stopped_early"] else 0


if __name__ == "__main__":
    args = sys.argv[1:]
    sys.exit(asyncio.run(main(args[0], dict(a.split("=", 1) for a in args[1:]))))

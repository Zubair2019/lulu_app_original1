# Design review: run `lulu-basics`

`com.democart.app` 1.0. This is the run's part of the project's register; `.uimatch/findings.md` lists every finding across runs. Ids are the same everywhere.
Last audit 20261124_161032: 3 screens of `20261124_160546` (democart_home).
Checked against Material 3, Android accessibility, WCAG 2.2 AA, Nielsen's heuristics and deceptive.design.

**0 waiting for your decision** · 0 approved · 0 fixed · 13 declined · 3 dismissed · 0 gone

## Declined: kept as designed

| ID | Severity | Finding | Note |
|---|---|---|---|
| R3 | medium | usability issue: Text smaller than 11 sp |  |
| R4 | medium | usability issue: Text smaller than 11 sp |  |
| R5 | medium | design flaw: Text below WCAG AA contrast |  |
| R6 | medium | usability issue: Text smaller than 11 sp |  |
| R7 | medium | design flaw: Text below WCAG AA contrast |  |
| R8 | medium | design flaw: Text below WCAG AA contrast |  |
| R9 | medium | design flaw: Text below WCAG AA contrast |  |
| R10 | medium | usability issue: Text smaller than 11 sp |  |
| R12 | low | inconsistency: Near-miss left alignment |  |
| R13 | low | usability issue: Touch targets smaller than 48 x 48 dp |  |
| R14 | low | usability issue: Touch targets smaller than 48 x 48 dp |  |
| R15 | low | friction: Touch targets closer than 8 dp |  |
| R16 | low | visual clutter: Too many text colours |  |

## Dismissed by the reviewer (false alarms)

| ID | Severity | Finding | Reason |
|---|---|---|---|
| R1 | high | usability issue: Touch targets smaller than 48 x 48 dp | R1/R2: the add buttons are cut off by the top edge of the screen in home_02, so only 12.4 dp of a 32 dp button is measured. R11: the flagged texts are banner artwork lettering (MEGA, up to, 70% OFF, FRESH15), not section titles. |
| R2 | high | usability issue: Touch targets smaller than 48 x 48 dp | R1/R2: the add buttons are cut off by the top edge of the screen in home_02, so only 12.4 dp of a 32 dp button is measured. R11: the flagged texts are banner artwork lettering (MEGA, up to, 70% OFF, FRESH15), not section titles. |
| R11 | low | poor hierarchy: Section titles not marked as headings | R1/R2: the add buttons are cut off by the top edge of the screen in home_02, so only 12.4 dp of a 32 dp button is measured. R11: the flagged texts are banner artwork lettering (MEGA, up to, 70% OFF, FRESH15), not section titles. |

## Measurements

| Measure | Your app | Source app |
|---|---|---|
| Touch targets under 48 dp | 4 | 9 |
| Smallest touch target (dp) | 12.4 | 18.2 |
| Texts below AA contrast | 5 | 2 |
| Lowest text contrast | 3.17 | 2.84 |
| Smallest text (sp, estimated) | 4.6 | 2.8 |
| Text sizes in use | 9 | 9 |
| Text colours in use | 11 | 21 |
| Headings marked | 0 | 16 |

Sizes and colours are read from screenshots: boxes within about 0.5 dp, text sizes within about 1 sp. Elements cut off by an edge, under a pinned bar or the status bar are left out of the measured checks.

Board: `D:\lulu_app\.uimatch\runs\lulu-basics\review\20261124_161032\board.png`


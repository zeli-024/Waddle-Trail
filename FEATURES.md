# Waddle Trail implementation map

Version 0.0.2 registers the categorized configuration, creates server storage,
tracks authoritative player positions, persists them atomically, and provides
`/wt status` plus the full `/waddletrail status` alias. It also synchronizes
bounded position batches and server-controlled locator settings to clients, then
renders player-skin heads above online players with range, occlusion, name, and
distance controls.

- `config`: three categorized, commented server-controlled configuration files.
- `storage`: private UUID maps, communal map data, player positions, and one backup.
- `sync`: private and communal transfer sequencing.
- `network`: versioned, bounded server-to-client position and locator-setting payloads.
- `fairplay`: the server-created Xaero profile named `Waddle Trail`.
- `locator`: authoritative online and offline positions, persisted without per-tick disk writes.
- `client/locator`: working in-world player-head rendering; Xaero map markers are next.
- `compat/xaero`: version-sensitive Xaero integration kept out of core logic.
- `command`: `/wt` and `/waddletrail` commands.

The supplied Xaero JARs are reference and test dependencies. They are deliberately
not redistributed in this repository.

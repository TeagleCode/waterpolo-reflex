# CI workflows (not yet active)

These build the APK and the Windows EXE and attach them to a GitHub Release when
you push a tag. They are parked here rather than in `.github/workflows/` because
GitHub refuses workflow files from an OAuth token that lacks the `workflow` scope,
and the token this repo was created with only has `repo`.

To turn them on:

```bash
gh auth refresh -h github.com -s workflow    # one-time, opens a browser
git mv ci/release.yml ci/pages.yml .github/workflows/
git commit -m "Enable CI" && git push
```

The four signing secrets (`KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`,
`KEY_PASSWORD`) are already set on the repo, so the APK will be release-signed with
the same key as the current release and will install as an update over it.

Until then, releases are built locally — see "Building locally" in the root README.

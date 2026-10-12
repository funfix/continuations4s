# How-to

## Releasing and publishing

### Snapshots

Set the version in `version.sbt`. Development versions must end in `-SNAPSHOT`.
Run `manual-publish` with a branch ref, such as `refs/heads/main`, and leave
`stable_version` disabled. The workflow runs `ci-snapshot` and publishes signed
JVM, Scala.js, and Scala Native artifacts to Central's snapshot repository.

Snapshot publishing must be enabled for the `org.funfix` namespace in the
[Central Portal](https://central.sonatype.com/).

### Stable releases

To prepare a stable release:

1. Set the release version in `version.sbt` without the `-SNAPSHOT` suffix.
2. Update both dependency examples in `README.md` to the release version.
3. Run `./sbt ci-test`.

Commit those changes and create and push the matching tag, such as `v1.2.3`.
Then run `manual-publish` with `ref_to_publish` set to `refs/tags/v1.2.3` and
enable `stable_version`. The workflow cross-publishes signed JVM, Scala.js, and
Scala Native artifacts and requests automatic publication to Central.

The same workflow builds Scaladoc and deploys it to
<https://continuations4s.funfix.org> through GitHub Pages.

## Repository secrets

Publishing requires these five Actions secrets:

- `PGP_KEY_ID`
- `PGP_PASSPHRASE`
- `PGP_SECRET`, containing the base64-encoded private key
- `SONATYPE_USERNAME`
- `SONATYPE_PASSWORD`

The Sonatype values are the generated Central Portal user-token credentials,
not the Central Portal login credentials. The signing key's public key must be
available from a key server supported by Central.

The agent requires `DEEPSEEK_API_KEY`. Renovate and the agent require
`AUTOMATION_APP_ID` and `AUTOMATION_APP_PRIVATE_KEY`.

## Automation GitHub App

Renovate and the agent use the existing organization-owned `Funfix` GitHub App.
Its installation includes `funfix/continuations4s`. Each workflow requests an
installation token scoped to this repository.

The app uses these permissions:

- Organization: `Members: read-only`
- Repository: `Administration: read-only`
- Repository: `Checks: read and write`
- Repository: `Commit statuses: read and write`
- Repository: `Contents: read and write`
- Repository: `Dependabot alerts: read-only`
- Repository: `Issues: read and write`
- Repository: `Pull requests: read and write`
- Repository: `Workflows: read and write`
- Repository: `Metadata: read-only`, granted automatically by GitHub

`Contents: read and write` permits Git pushes. `Workflows: read and write` is
needed when automation changes files under `.github/workflows`.

## Agent workflow

Pi uses DeepSeek's API and the Funfix credentials above. Add the repository
Actions secret `DEEPSEEK_API_KEY`.

Users with write access or higher can start an issue or PR conversation comment
with `/agent`, followed by a request:

```text
/agent Please take care of this issue, but pin the JDK version to 21.
/agent Please review this PR, especially continuation semantics and binary compatibility.
```

The agent can review contributor PRs, but cannot push to their forks. On fork PRs,
use a conversation comment, not an inline comment.

Edit `.github/workflows/agent.md`, then regenerate the lock file with
[gh-aw v0.91.7](https://github.com/github/gh-aw/releases/tag/v0.91.7):

```bash
gh aw compile agent --strict --validate
```

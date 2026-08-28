module.exports = {
  platform: "github",
  repositories: ["funfix/continuations4s"],
  branchPrefix: "renovate/",
  onboarding: false,
  requireConfig: "optional",
  recreateWhen: "always",
  prHourlyLimit: 0,
  separateMajorMinor: false,

  extends: [":dependencyDashboard"],

  enabledManagers: ["github-actions", "sbt"],

  ignorePaths: ["**/.gradle/**"],

  packageRules: [
    {
      description: "Group all dependency updates into a single PR",
      matchManagers: ["github-actions", "sbt"],
      groupName: "dependencies",
      groupSlug: "all-dependencies",
      group: {
        commitMessageTopic: "dependencies",
        commitMessageExtra: "",
      },
    },
    {
      description: "Only use stable dotted numeric JVM dependency versions",
      matchManagers: ["sbt"],
      allowedVersions: "/^\\d+(?:\\.\\d+)+$/",
    },
    {
      description: "Keep sbt on the 1.x line (2.x is not yet supported)",
      matchManagers: ["sbt"],
      matchPackageNames: ["sbt/sbt"],
      allowedVersions: "/^1\\.\\d+\\.\\d+$/",
    },
    {
      matchManagers: ["sbt"],
      matchPackageNames: ["org.scala-lang:scala3-library_3"],
      separateMinorPatch: false,
    },
    {
      description: "Keep wartremover below 3.6.x until it is cross-published for Scala 3.8",
      matchManagers: ["sbt"],
      matchPackageNames: ["org.wartremover:sbt-wartremover"],
      allowedVersions: "/^3\\.5\\.\\d+$/",
    },
    {
      description: "Disable updates for libraryDependencySchemes entries (not real versions)",
      matchManagers: ["sbt"],
      matchCurrentValue: "/^(early-semver|semver-spec|pvp|always|strict)$/",
      enabled: false,
    },
    {
      description: "Wait one week before proposing dependency updates",
      matchManagers: ["github-actions", "sbt"],
      minimumReleaseAge: "7 days",
      minimumReleaseAgeBehaviour: "timestamp-optional",
    },
  ],
};

package com.bawnorton.neruina.report;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.thread.ThreadUtils;
import com.bawnorton.neruina.util.TickingEntry;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resource.Resource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class AutoReportHandler {
    /*? if >=1.19 {*/
    private final Set<TickingEntry> reportedEntries = new HashSet<>();
    private final List<AutoReportConfig> configs = new ArrayList<>();
    private final Map<String, RepositoryReference> repositories = new HashMap<>();
    private AutoReportConfig masterConfig;

    public void init(MinecraftServer server) {
        Map<Identifier, Resource> neruinaAutoGhFiles = server.getResourceManager().findResources(Neruina.MOD_ID, (resource) -> resource.getPath().equals("%s/auto_report.json".formatted(Neruina.MOD_ID)));
        for (Map.Entry<Identifier, Resource> entry : neruinaAutoGhFiles.entrySet()) {
            Identifier id = entry.getKey();
            Resource resource = entry.getValue();
            try (JsonReader reader = new JsonReader(resource.getReader())) {
                AutoReportConfig config = AutoReportConfig.fromJson(reader);
                if (config.isVaild()) {
                    if (config.modid().equals(Neruina.MOD_ID)) {
                        masterConfig = config;
                        continue;
                    }
                    configs.add(config);
                } else {
                    Neruina.LOGGER.warn("Invalid auto report config found: {}, ignoring", id);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public CompletableFuture<Pair<ReportCode, String>> createReports(MinecraftServer server, TickingEntry entry) {
        return GithubAuthManager.getOrLogin().thenApply(github -> {
            if (reportedEntries.contains(entry)) {
                return Pair.of(ReportCode.ALREADY_EXISTS, "");
            }

            reportedEntries.add(entry);
            Set<String> modids = entry.findPotentialSources();
            Map<String, @Nullable GHIssue> issues = new HashMap<>();
            modids.forEach(modid -> {
                issues.put(modid, null);
                RepositoryReference repository = repositories.computeIfAbsent(modid, key -> {
                    for (AutoReportConfig config : configs) {
                        String listeningModid = config.modid();
                        if (!listeningModid.equals("*") && !listeningModid.equals(modid)) continue;

                        try {
                            GHRepository ghRepository = github.getRepository(config.repo());
                            return new RepositoryReference(modid, ghRepository, config);
                        } catch (IOException e) {
                            Neruina.LOGGER.error(
                                    "Failed to get repository for mod: \"{}\", report this to them.",
                                    modid,
                                    e
                            );
                        }
                    }
                    return null;
                });
                if (repository == null) return;

                GHIssue issue = ThreadUtils.onThread(server, () -> createIssue(repository, entry));
                if (issue != null) {
                    issues.put(modid, issue);
                }
            });
            GHIssue masterIssue = ThreadUtils.onThread(server, () -> createMasterIssue(github, issues, entry));
            String url = masterIssue != null ? masterIssue.getHtmlUrl().toString() : null;
            return Pair.of(ReportCode.SUCCESS, url);
        }).thenApply(result -> {
            Neruina.LOGGER.info(
                    "Report(s) created for ticking entry: ({}: {})",
                    entry.getCauseType(),
                    entry.getCauseName()
            );
            return result;
        }).exceptionally(throwable -> {
            Neruina.LOGGER.error("Failed to create report(s)", throwable);
            return Pair.of(ReportCode.FAILURE, "");
        });
    }

    private GHIssue createMasterIssue(GitHub github, Map<String, GHIssue> issueMap, TickingEntry tickingEntry) {
        if (masterConfig == null) return null;

        RepositoryReference masterRepo = repositories.computeIfAbsent(Neruina.MOD_ID, key -> {
            try {
                GHRepository ghRepository = github.getRepository(masterConfig.repo());
                return new RepositoryReference(Neruina.MOD_ID, ghRepository, masterConfig);
            } catch (IOException e) {
                return null;
            }
        });
        if (masterRepo == null) return null;

        IssueFormatter formatter = masterConfig.createIssueFormatter();
        String body = "%s".formatted(formatter.getBody(tickingEntry));
        if (!issueMap.isEmpty()) {
            body = """
                    ## Associated Issues:
                    %s
                    
                    %s
                    """.formatted(
                    issueMap.entrySet()
                            .stream()
                            .map(entry -> {
                                String modid = entry.getKey();
                                GHIssue issue = entry.getValue();
                                if (issue == null) {
                                    return "- %s: Not opted into auto-reporting".formatted(modid);
                                }
                                return "- [%s](%s)".formatted(modid, issue.getHtmlUrl().toString());
                            })
                            .collect(Collectors.joining("\n")),
                    body
            );
        }
        GHIssueBuilder builder = masterRepo.createIssueBuilder(formatter.getTitle(tickingEntry)).body(body);
        try {
            return builder.create();
        } catch (IOException e) {
            Neruina.LOGGER.error("Failed to create master issue", e);
            return null;
        }
    }

    private GHIssue createIssue(RepositoryReference reference, TickingEntry entry) {
        AutoReportConfig config = reference.config();
        IssueFormatter formatter = config.createIssueFormatter();
        GHIssueBuilder builder = reference.createIssueBuilder(formatter.getTitle(entry))
                .body(formatter.getBody(entry));
        try {
            return builder.create();
        } catch (IOException e) {
            Neruina.LOGGER.error("Failed to create issue for mod: \"{}\", report this to them.", reference.modid(), e);
            return null;
        }
    }
    /*?}*/
}

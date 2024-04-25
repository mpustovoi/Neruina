package com.bawnorton.neruina.report;

import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;

public record RepositoryReference(String modid, GHRepository githubRepo, AutoReportConfig config) {
    /*? if >=1.19 {*/
    public GHIssueBuilder createIssueBuilder(String title) {
        return githubRepo.createIssue(title);
    }
    /*?}*/
}

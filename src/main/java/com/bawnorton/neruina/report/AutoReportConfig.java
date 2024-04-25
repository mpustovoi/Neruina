package com.bawnorton.neruina.report;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public record AutoReportConfig(String modid, String repo, String title, String body) {
    /*? if >=1.19 {*/
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public boolean isVaild() {
        return modid != null && repo != null;
    }

    public IssueFormatter createIssueFormatter() {
        return new IssueFormatter(this);
    }

    public static AutoReportConfig fromJson(JsonReader reader) {
        return GSON.fromJson(reader, AutoReportConfig.class);
    }
    /*?}*/
}

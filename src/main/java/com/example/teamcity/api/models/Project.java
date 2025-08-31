package com.example.teamcity.api.models;

import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.annotations.Random;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends BaseModel {
    @Random
    private String id;
    @Random
    private String name;
    private String description;
    private ParentProject parentProject;

    private Boolean copyAllAssociatedSettings;
    @Optional
    private SourceProject sourceProject;
}

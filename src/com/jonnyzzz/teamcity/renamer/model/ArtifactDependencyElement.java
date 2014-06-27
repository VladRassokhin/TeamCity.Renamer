package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.resolve.Visitors;
import com.jonnyzzz.teamcity.renamer.resolve.buildTypes.BuildTypeReferenceConverter;
import org.jetbrains.annotations.Nullable;

public abstract class ArtifactDependencyElement extends TeamCityElement implements AutoFoldableElement {
  @Attribute("sourceBuildTypeId")
  @Convert(value = BuildTypeReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getSourceBuildTypeId();

  @SubTag("artifact")
  public abstract ArtifactElement getArtifact();

  @Nullable
  @Override
  public String getFoldedText() {
    final GenericAttributeValue<String> ref = getSourceBuildTypeId();
    final String depId = ref.getStringValue();
    if (depId == null) return null;

    final BuildTypeFile buildType = Visitors.findBuildType(ref, depId);
    if (buildType == null) {
      return null;
    }

    return buildType.getFilePresentableNameText();
  }
}

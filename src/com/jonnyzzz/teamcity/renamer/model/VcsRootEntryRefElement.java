package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import com.jonnyzzz.teamcity.renamer.folding.AutoFoldableElement;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import com.jonnyzzz.teamcity.renamer.resolve.vcsRoot.VcsRootReferenceConverter;
import com.jonnyzzz.teamcity.renamer.resolve.vcsRoot.VcsRoots;
import org.jetbrains.annotations.Nullable;

public abstract class VcsRootEntryRefElement extends TeamCityElement implements AutoFoldableElement {

  @Required
  @Attribute("root-id")
  @Convert(value = VcsRootReferenceConverter.class, soft = false)
  public abstract GenericAttributeValue<String> getVcsRootId();

  @Nullable
  @Override
  public String getFoldedText() {
    final GenericAttributeValue<String> id = getVcsRootId();
    if (id == null) return null;
    final String rootId = id.getStringValue();

    final VcsRootFile root = VcsRoots.resolve(this, rootId);
    if (root == null) return null;

    final GenericAttributeValue<String> rootName = root.getVcsRootName();
    if (rootName != null) {
      final String rootNameString = rootName.getStringValue();
      if (rootNameString != null) return rootNameString;
    }
    return rootId;

  }
}

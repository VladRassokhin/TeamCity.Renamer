package com.jonnyzzz.teamcity.renamer.model.project;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.resolve.DeclaredProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public abstract class ProjectFile extends TeamCityFile {

  @Attribute("parent-id")
  public abstract GenericAttributeValue<String> getParentProjectId();

  @SubTag("parameters")
  public abstract ParametersBlockElement getParametersBlock();


  @NotNull
  @Override
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    return getParametersBlock().getDeclarations();
  }

  @Nullable
  @Override
  public ProjectFile getParentProjectFile() {
    final GenericAttributeValue<String> parentProjectAttribute = getParentProjectId();
    if (parentProjectAttribute == null) return null;

    final String parentProjectId = parentProjectAttribute.getStringValue();
    if (parentProjectId == null || parentProjectId.trim().length() == 0) return null;

    final XmlElement xmlElement = getXmlElement();
    if (xmlElement == null) return null;

    final PsiFile containingFile = xmlElement.getContainingFile();
    if (containingFile == null) return null;

    final PsiDirectory containingDir = containingFile.getParent();
    if (containingDir == null) return null;

    final PsiDirectory parentDir = containingDir.findSubdirectory(parentProjectId);
    if (parentDir == null) return null;

    final PsiFile projectFile = parentDir.findFile("project-config.xml");
    if (projectFile == null) return null;
    if (!(projectFile instanceof XmlFile)) return null;

    final XmlFile xmlFile = (XmlFile) projectFile;
    final DomElement projectXml = DomManager.getDomManager(xmlElement.getProject()).getDomElement(xmlFile.getRootTag());

    if (projectXml == null) return null;
    if (!(projectXml instanceof ProjectFile)) return null;

    return (ProjectFile) projectXml;
  }
}

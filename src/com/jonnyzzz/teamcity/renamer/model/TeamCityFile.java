package com.jonnyzzz.teamcity.renamer.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.spellchecker.xml.NoSpellchecking;
import com.intellij.util.xml.*;
import com.jonnyzzz.teamcity.renamer.model.buildType.BuildTypeFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.resolve.property.DeclaredProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
@NoSpellchecking
public abstract class TeamCityFile extends TeamCityElement {
  public static final String PROJECT_CONFIG_FILE_NAME = "project-config.xml";

  @SubTag("name")
  public abstract GenericDomValue<String> getFileName();

  @SubTag("description")
  public abstract GenericDomValue<String> getFileDescription();

  @NotNull
  public final String getFilePresentableNameHTML() {
    return getFileKind()
            + " <b>" +
            StringUtil.escapeXml(getPresentableName())
            + "</b>(" + getFileId() + ")";
  }

  @NotNull
  public final String getFilePresentableNameText() {
    return getFileKind()
            + " " +
            (getPresentableName())
            + "(" + getFileId() + ")";
  }

  @NotNull
  protected final String getPresentableName() {
    final String thisName = getFileName().getRawText();
    final ProjectFile parentFile = getParentProjectFile();
    if (parentFile == null) return thisName;
    return parentFile.getPresentableName() + " :: " + thisName;
  }

  @NotNull
  public String getFileKind() {
    throw new RuntimeException("Must be implemented");
  }


  @Nullable
  public String getFileId() {
    throw new RuntimeException("Must be implemented");
  }

  /**
   * @return containing project file. For ProjectFile returns parent project (if any)
   */
  @Nullable
  public ProjectFile getParentProjectFile() {
    throw new RuntimeException("Must be implemented");
  }

  @NotNull
  public Iterable<DeclaredProperty> getDeclaredParameters() {
    throw new RuntimeException("Must be implemented");
  }

  @Override
  public final String toString() {
    return "[" + getFileId() + "] " + super.toString();
  }


  @Nullable
  public static <T extends TeamCityFile> T toTeamCityFile(@NotNull final Class<T> clazz,
                                                          @Nullable final PsiFile psiFile) {
    if (psiFile == null) return null;
    if (!psiFile.getName().endsWith(".xml")) return null;
    if (!(psiFile instanceof XmlFile)) return null;

    final XmlFile xmlFile = (XmlFile) psiFile;
    final Project project = psiFile.getProject();
    final DomElement xml = DomManager
            .getDomManager(project)
            .getDomElement(xmlFile.getRootTag());

    if (xml == null) return null;
    if (!clazz.isInstance(xml)) return null;
    return clazz.cast(xml);
  }

  @Nullable
  public static <T extends TeamCityElement> T toTeamCityElement(@NotNull final Class<T> clazz,
                                                                @Nullable final PsiElement psiElement) {

    if (psiElement == null) return null;
    final DomElement xml = findContainingDomElement(psiElement);

    if (xml == null) return null;
    if (!clazz.isInstance(xml)) return null;
    return clazz.cast(xml);
  }

  @Nullable
  public static DomElement findContainingDomElement(@NotNull final PsiElement psiElement) {
    final DomManager domManager = DomManager.getDomManager(psiElement.getProject());

    if (psiElement instanceof XmlTag) {
      return domManager.getDomElement((XmlTag) psiElement);
    }

    if (psiElement instanceof XmlAttribute) {
      return domManager.getDomElement((XmlAttribute) psiElement);
    }

    if (psiElement instanceof XmlFile) {
      DomFileElement<TeamCityFile> fileElement = domManager.getFileElement((XmlFile) psiElement, TeamCityFile.class);
      if (fileElement != null)
        return fileElement.getRootElement();
    }

    final XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(psiElement, XmlAttribute.class);
    if (xmlAttribute != null) {
      return domManager.getDomElement(xmlAttribute);
    }

    final XmlTag tag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
    if (tag != null) {
      return domManager.getDomElement(tag);
    }
    return null;
  }

  @Nullable
  public BuildTypeFile findBuildTypeById(@NotNull String buildTypeId) {
    ProjectFile projectFile = getParentProjectFile();
    if (projectFile == null)
      return null;

    for (BuildTypeFile f : projectFile.getAllBuildTypes()) {
      if (buildTypeId.equals(f.getFileId()))
        return f;
    }
    return null;
  }

}

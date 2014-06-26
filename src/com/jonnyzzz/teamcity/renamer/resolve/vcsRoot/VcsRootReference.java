package com.jonnyzzz.teamcity.renamer.resolve.vcsRoot;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.GenericDomValue;
import com.jonnyzzz.teamcity.renamer.model.TeamCityFile;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import com.jonnyzzz.teamcity.renamer.model.vcsRoot.VcsRootFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class VcsRootReference extends PsiReferenceBase<PsiElement> {
  private final GenericDomValue<String> myAttr;

  public VcsRootReference(@NotNull GenericDomValue<String> attr, @NotNull PsiElement element) {
    super(element);
    myAttr = attr;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    TeamCityFile file = myAttr.getParentOfType(TeamCityFile.class, false);
    if (file == null)
      return null;

    ProjectFile projectFile = file.getParentProjectFile();
    if (projectFile == null)
      return null;

    String value = myAttr.getValue();
    if (value == null)
      return null;

    for (final VcsRootFile f : projectFile.getAllVcsRoots()) {
      if (value.equals(f.getFileId())) {
        final XmlElement xmlElement = f.getXmlElement();
        if (xmlElement == null)
          continue;

        final PsiFile containingFile = xmlElement.getContainingFile();
        return new RenameableVcsRootFileElement(containingFile);
      }
    }
    return null;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    if (newElementName == null)
      return super.handleElementRename(newElementName);

    if (newElementName.endsWith(".xml")) {
      String name = newElementName.substring(0, newElementName.length() - 4);
      return super.handleElementRename(name);
    }

    return super.handleElementRename(newElementName);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  private static class RenameableVcsRootFileElement extends RenameableFakePsiElement {
    private final PsiFile myContainingFile;

    public RenameableVcsRootFileElement(PsiFile containingFile) {
      super(containingFile);
      myContainingFile = containingFile;
    }

    @Override
    public String getName() {
      return FileUtil.getNameWithoutExtension(myContainingFile.getName());
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
      myContainingFile.setName(name + ".xml");
      return this;
    }

    @Override
    public String getTypeName() {
      return "vcsRoot";
    }

    @Nullable
    @Override
    public Icon getIcon() {
      return null;
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
      if (another instanceof RenameableVcsRootFileElement) {
        return myContainingFile == ((RenameableVcsRootFileElement)another).myContainingFile;
      }
      return myContainingFile == another;
    }
  }
}

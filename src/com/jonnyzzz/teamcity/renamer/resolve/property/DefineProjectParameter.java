package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import com.jonnyzzz.teamcity.renamer.model.ParametersBlockElement;
import com.jonnyzzz.teamcity.renamer.model.project.ProjectFile;
import org.jetbrains.annotations.NotNull;

public class DefineProjectParameter extends AbstractDefineNewParameterQuickFix {
  private final ProjectFile myProjectFile;
  private final String myText;

  public DefineProjectParameter(String name, ProjectFile projectFile, String text) {
    super(name);
    myProjectFile = projectFile;
    myText = text;
  }

  @Override
  protected ParametersBlockElement getWhereToPlace(ParameterElement element) {
    return myProjectFile.getParametersBlock();
  }

  @NotNull
  @Override
  public String getName() {
    return myText;
  }
}

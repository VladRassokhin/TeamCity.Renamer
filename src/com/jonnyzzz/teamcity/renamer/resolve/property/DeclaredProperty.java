package com.jonnyzzz.teamcity.renamer.resolve.property;

import com.google.common.base.Function;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.GenericAttributeValue;
import com.jonnyzzz.teamcity.renamer.model.ParameterElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 */
public class DeclaredProperty {
  @NotNull
  private final ParameterElement myParameterElement;
  private final String myName;
  private final PsiElement myResolvedValue;

  public DeclaredProperty(@NotNull final ParameterElement parameterElement,
                          @NotNull final String name,
                          @NotNull final PsiElement resolvedValue) {
    myParameterElement = parameterElement;
    myName = name;
    myResolvedValue = resolvedValue;
  }

  @NotNull
  public String getName() {
    return myName;
  }

  @NotNull
  public PsiElement getResolvedValue() {
    return myResolvedValue;
  }

  @Nullable
  public String getRawValue() {
    return myParameterElement.getStringValue();
  }


  public static Function<ParameterElement, DeclaredProperty> FROM_PARAMETER_ELEMENT = new Function<ParameterElement, DeclaredProperty>() {
    @Override
    public DeclaredProperty apply(final ParameterElement parameterElement) {
      final GenericAttributeValue<String> nameDeclaration = parameterElement.getParameterName();
      if (nameDeclaration == null) return null;

      final String name = parameterElement.getParameterNameString();
      if (name == null) return null;

      final XmlAttributeValue element = nameDeclaration.getXmlAttributeValue();
      if (element == null) return null;

      return new DeclaredProperty(parameterElement, name, element);
    }
  };

  @NotNull
  public ParameterElement getParameterElement() {
    return myParameterElement;
  }
}

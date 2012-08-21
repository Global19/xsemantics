package it.xsemantics.dsl.generator;

import com.google.inject.Inject;
import it.xsemantics.dsl.generator.XsemanticsXbaseCompiler;
import it.xsemantics.dsl.xsemantics.ErrorSpecification;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;

@SuppressWarnings("all")
public class XsemanticsErrorSpecificationGenerator {
  @Inject
  private XsemanticsXbaseCompiler xbaseCompiler;
  
  public String compileErrorOfErrorSpecification(final ErrorSpecification errorSpecification, final ITreeAppendable appendable) {
    String _compileErrorOfErrorSpecification = this.xbaseCompiler.compileErrorOfErrorSpecification(errorSpecification, appendable);
    return _compileErrorOfErrorSpecification;
  }
  
  public String compileSourceOfErrorSpecification(final ErrorSpecification errorSpecification, final ITreeAppendable appendable) {
    String _compileSourceOfErrorSpecification = this.xbaseCompiler.compileSourceOfErrorSpecification(errorSpecification, appendable);
    return _compileSourceOfErrorSpecification;
  }
  
  public String compileFeatureOfErrorSpecification(final ErrorSpecification errorSpecification, final ITreeAppendable appendable) {
    String _compileFeatureOfErrorSpecification = this.xbaseCompiler.compileFeatureOfErrorSpecification(errorSpecification, appendable);
    return _compileFeatureOfErrorSpecification;
  }
}

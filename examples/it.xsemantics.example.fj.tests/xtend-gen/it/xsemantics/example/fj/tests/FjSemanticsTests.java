package it.xsemantics.example.fj.tests;

import com.google.common.base.Objects;
import com.google.inject.Inject;
import it.xsemantics.example.fj.fj.ClassType;
import it.xsemantics.example.fj.fj.Expression;
import it.xsemantics.example.fj.fj.Method;
import it.xsemantics.example.fj.fj.MethodBody;
import it.xsemantics.example.fj.fj.Parameter;
import it.xsemantics.example.fj.fj.Program;
import it.xsemantics.example.fj.fj.Selection;
import it.xsemantics.example.fj.fj.Type;
import it.xsemantics.example.fj.tests.FjBaseTests;
import it.xsemantics.example.fj.tests.FjInjectorProviderCustom;
import it.xsemantics.example.fj.typing.FjTypeSystem;
import it.xsemantics.example.fj.util.FjSemanticsUtils;
import it.xsemantics.example.fj.util.FjTypeUtils;
import it.xsemantics.runtime.Result;
import it.xsemantics.runtime.RuleEnvironment;
import it.xsemantics.runtime.RuleFailedException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value = XtextRunner.class)
@InjectWith(value = FjInjectorProviderCustom.class)
@SuppressWarnings("all")
public class FjSemanticsTests extends FjBaseTests {
  @Inject
  private FjSemanticsUtils _fjSemanticsUtils;
  
  @Inject
  private FjTypeSystem fjSystem;
  
  @Test
  public void testLiteralsAreValues() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("10");
    this.assertValue(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("true");
    this.assertValue(_builder_1);
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("\"foo\"");
    this.assertValue(_builder_2);
  }
  
  @Test
  public void testNewWithNoArgsIsValue() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A()");
    _builder.newLine();
    this.assertValue(_builder);
  }
  
  @Test
  public void testNewWithEvaluatedArgsIsValue() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B b;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class B {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new B(1))");
    _builder.newLine();
    this.assertValue(_builder);
  }
  
  @Test
  public void testMethodCallIsNotValue() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String m() { return \'foo\'; }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A().m()");
    _builder.newLine();
    this.assertNotValue(_builder);
  }
  
  @Test
  public void testReplaceThis() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class B { }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B o;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("A m(A a, int i) { return this.m(this, 10); }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new B())");
    _builder.newLine();
    this.assertThisReplacement(_builder, "new A(new B()).m(new A(new B()), 10)");
  }
  
  @Test
  public void testReplaceParams() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class B { }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B o;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("A m(A a, B b, int i, String s) { return a.m(a, b, i, s); }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new B()).m(new A(new B()), new A(new B()).o, 10, \'foo\')");
    _builder.newLine();
    this.assertParamsReplacement(_builder, 
      "new A(new B()).m(new A(new B()), new A(new B()).o, 10, \'foo\')");
  }
  
  @Test
  public void testReplaceThisAndParams() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class B { }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B o;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("A m(A a, B b, int i, String s) { return this.m(a, b, i, s); }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new B()).m(new A(new B()), new A(new B()).o, 10, \'foo\')");
    _builder.newLine();
    this.assertThisAndParamsReplacement(_builder, 
      "new A(new B()).m(new A(new B()), new A(new B()).o, 10, \'foo\')");
  }
  
  @Test
  public void testWellTypedAfterSubstitutionParam() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class Base { }");
    _builder.newLine();
    _builder.append("class B extends Base { }");
    _builder.newLine();
    _builder.append("class C extends B {}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B o;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("Base m(B b, int i) { ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return b;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new B()).m(new C(), 10)");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("WELLTYPED METHOD BODY");
    _builder_1.newLine();
    _builder_1.append("TParamRef: [this <- A] |- b : B");
    _builder_1.newLine();
    _builder_1.append("WELLTYPED AFTER SUBSTITUTION");
    _builder_1.newLine();
    _builder_1.append("TNew: [] |- new C() : C");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("SubtypeSequence: [] |- new C() ~> [] << []");
    _builder_1.newLine();
    _builder_1.append("SUBTYPE AFTER SUBSTITUTION");
    _builder_1.newLine();
    _builder_1.append("ClassSubtyping: [] |- C <: B");
    this.assertSubstitutionLemma(_builder, _builder_1);
  }
  
  @Test
  public void testWellTypedAfterSubstitutionThis() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class B extends A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("A m() { ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return this;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class C extends B {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new C().m()");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("WELLTYPED METHOD BODY");
    _builder_1.newLine();
    _builder_1.append("TThis: [this <- B] |- this : B");
    _builder_1.newLine();
    _builder_1.append("WELLTYPED AFTER SUBSTITUTION");
    _builder_1.newLine();
    _builder_1.append("TNew: [] |- new C() : C");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("SubtypeSequence: [] |- new C() ~> [] << []");
    _builder_1.newLine();
    _builder_1.append("SUBTYPE AFTER SUBSTITUTION");
    _builder_1.newLine();
    _builder_1.append("ClassSubtyping: [] |- C <: B");
    this.assertSubstitutionLemma(_builder, _builder_1);
  }
  
  @Test
  public void testWellTypedAfterSubstitutionFieldSelection() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class B extends A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int m() { ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return this.i;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class C extends B {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new C(10).m()");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("WELLTYPED METHOD BODY");
    _builder_1.newLine();
    _builder_1.append("TSelection: [this <- B] |- this.i : int");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("TThis: [this <- B] |- this : B");
    _builder_1.newLine();
    _builder_1.append("WELLTYPED AFTER SUBSTITUTION");
    _builder_1.newLine();
    _builder_1.append("TSelection: [] |- new C(10).i : int");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("TNew: [] |- new C(10) : C");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("SubtypeSequence: [] |- new C(10) ~> [10] << [int i;]");
    _builder_1.newLine();
    _builder_1.append("   ");
    _builder_1.append("ExpressionAssignableToType: [] |- 10 <| int");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("TIntConstant: [] |- 10 : int");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("BasicSubtyping: [] |- int <: int");
    _builder_1.newLine();
    _builder_1.append("SUBTYPE AFTER SUBSTITUTION");
    _builder_1.newLine();
    _builder_1.append("BasicSubtyping: [] |- int <: int");
    this.assertSubstitutionLemma(_builder, _builder_1);
  }
  
  @Test
  public void testReduceNew() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A()");
    _builder.newLine();
    this.assertReduceOneStep(_builder, "new A()", null);
  }
  
  @Test
  public void testReduceFieldSelection() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(10).i");
    _builder.newLine();
    this.assertReduceOneStep(_builder, "10", null);
  }
  
  @Test
  public void testReduceMethodSelection() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class B { String s; }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B o;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B m(B b, String s) { ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return this.getB(s);");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B getB(String s) { return this.o; }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new B(\'foo\')).m(new B(\'bar\'), \'aaa\')");
    _builder.newLine();
    this.assertReduceOneStep(_builder, 
      "new A(new B(\'foo\')).getB(\'aaa\')", null);
  }
  
  @Test
  public void testReduceCast() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class B extends A { }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("(A) new B(10)");
    _builder.newLine();
    this.assertReduceOneStep(_builder, "new B(10)", null);
  }
  
  @Test
  public void testReduceCastWrong() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class B extends A { }");
    _builder.newLine();
    _builder.newLine();
    _builder.append("(B) new A(10)");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("failed: RCast: [] |- (B) new A(10) ~> Expression");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("failed: new A(10) is not assignable for B");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("failed: A is not a subtype of B");
    _builder_1.newLine();
    _builder_1.append("   ");
    _builder_1.append("failed: getAll(left.classref, FjPackage::eINSTANCE.class_Superclass, FjPackage::eINSTANCE.class_Superclass, typeof(Class)) .contains(right.classref)");
    this.assertReduceWrong(_builder, _builder_1);
  }
  
  @Test
  public void testCongruenceReduceFieldSelection() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String s;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new A(10, \'a\').i, \'b\').i");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("RSelection: [] |- new A(new A(10, \'a\').i, \'b\').i ~> new A(10, \'b\').i");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("RNew: [] |- new A(new A(10, \'a\').i, \'b\') ~> new A(10, \'b\')");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("RSelection: [] |- new A(10, \'a\').i ~> 10");
    this.assertReduceOneStep(_builder, 
      "new A(10, \'b\').i", _builder_1);
  }
  
  @Test
  public void testCongruenceReduceMethodSelection() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String s;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int m(int arg) { return arg; }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(10, \'b\').m(new A(20, \'c\').i)");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("RSelection: [] |- new A(10, \'b\').m(new A(20, \'c\').i) ~> new A(10, \'b\').m(20)");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("RSelection: [] |- new A(20, \'c\').i ~> 20");
    this.assertReduceOneStep(_builder, 
      "new A(10, \'b\').m(20)", _builder_1);
  }
  
  @Test
  public void testCongruenceReduceCast() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B createB() { return new B(100); }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class B extends A { ");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("(A) (new A(10).createB())");
    _builder.newLine();
    this.assertReduceOneStep(_builder, "(A) new B(100)", null);
  }
  
  @Test
  public void testReduceAllFieldSelection() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("boolean b;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String s;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new A(10, true, \'a\').i, false, \'b\').b");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("RSelection: [] |- new A(new A(10, true, \'a\').i, false, \'b\').b ~> new A(10, false, \'b\').b");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("RNew: [] |- new A(new A(10, true, \'a\').i, false, \'b\') ~> new A(10, false, \'b\')");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("RSelection: [] |- new A(10, true, \'a\').i ~> 10");
    _builder_1.newLine();
    _builder_1.append("RSelection: [] |- new A(10, false, \'b\').b ~> false");
    this.assertReduceAll(_builder, _builder_1);
  }
  
  @Test
  public void testReduceAllMethodSelection() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class B { ");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String s;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("String getS() { return this.s; }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B o;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B m(B b, String s) { ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return this.getB(s);");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B getB(String s) { ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("return this.o;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("new A(new B(\'foo\')).m(new B(\'bar\'), \'aaa\').getS()");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("RSelection: [] |- new A(new B(\'foo\')).m(new B(\'bar\'), \'aaa\').getS() ~> new A(new B(\'foo\')).getB(\'aaa\').getS()");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("RSelection: [] |- new A(new B(\'foo\')).m(new B(\'bar\'), \'aaa\') ~> new A(new B(\'foo\')).getB(\'aaa\')");
    _builder_1.newLine();
    _builder_1.append("RSelection: [] |- new A(new B(\'foo\')).getB(\'aaa\').getS() ~> new A(new B(\'foo\')).o.getS()");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("RSelection: [] |- new A(new B(\'foo\')).getB(\'aaa\') ~> new A(new B(\'foo\')).o");
    _builder_1.newLine();
    _builder_1.append("RSelection: [] |- new A().o.getS() ~> new B(\'foo\').getS()");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("RSelection: [] |- new A(new B(\'foo\')).o ~> new B(\'foo\')");
    _builder_1.newLine();
    _builder_1.append("RSelection: [] |- new B(\'foo\').getS() ~> new B(\'foo\').s");
    _builder_1.newLine();
    _builder_1.append("RSelection: [] |- new B(\'foo\').s ~> \'foo\' ");
    this.assertReduceAll(_builder, _builder_1);
  }
  
  @Test
  public void testReduceAllCast() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class A {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("int i;");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("B createB() { return new B(100); }");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("class B extends A { ");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("(A) (new A(10).createB())");
    _builder.newLine();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("RCast: [] |- (A) new A(10).createB() ~> (A) new B(100)");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("RSelection: [] |- new A(10).createB() ~> new B(100)");
    _builder_1.newLine();
    _builder_1.append("RCast: [] |- (A) new B(100) ~> new B(100)");
    _builder_1.newLine();
    _builder_1.append(" ");
    _builder_1.append("ExpressionAssignableToType: [] |- new B(100) <| A");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("TNew: [] |- new B(100) : B");
    _builder_1.newLine();
    _builder_1.append("   ");
    _builder_1.append("SubtypeSequence: [] |- new B(100) ~> [100] << [int i;]");
    _builder_1.newLine();
    _builder_1.append("    ");
    _builder_1.append("ExpressionAssignableToType: [] |- 100 <| int");
    _builder_1.newLine();
    _builder_1.append("     ");
    _builder_1.append("TIntConstant: [] |- 100 : int");
    _builder_1.newLine();
    _builder_1.append("     ");
    _builder_1.append("BasicSubtyping: [] |- int <: int");
    _builder_1.newLine();
    _builder_1.append("  ");
    _builder_1.append("ClassSubtyping: [] |- B <: A");
    this.assertReduceAll(_builder, _builder_1);
  }
  
  private void assertValue(final CharSequence prog) {
    Program _parseAndAssertNoError = this.parseAndAssertNoError(prog);
    Expression _main = _parseAndAssertNoError.getMain();
    boolean _isValue = this._fjSemanticsUtils.isValue(_main);
    Assert.assertTrue(_isValue);
  }
  
  private void assertNotValue(final CharSequence prog) {
    Program _parseAndAssertNoError = this.parseAndAssertNoError(prog);
    Expression _main = _parseAndAssertNoError.getMain();
    boolean _isValue = this._fjSemanticsUtils.isValue(_main);
    Assert.assertFalse(_isValue);
  }
  
  private void assertThisReplacement(final CharSequence prog, final CharSequence expected) {
    final Program p = this.parseAndAssertNoError(prog);
    Method _methodByName = this.methodByName(p, "m");
    MethodBody _body = _methodByName.getBody();
    Expression _expression = _body.getExpression();
    final Expression mBodyExp = EcoreUtil.<Expression>copy(_expression);
    Expression _main = p.getMain();
    this._fjSemanticsUtils.replaceThis(mBodyExp, _main);
    String _string = expected.toString();
    String _string_1 = this.stringRep.string(mBodyExp);
    Assert.assertEquals(_string, _string_1);
  }
  
  private void assertParamsReplacement(final CharSequence prog, final CharSequence expected) {
    final Program p = this.parseAndAssertNoError(prog);
    final Method m = this.methodByName(p, "m");
    MethodBody _body = m.getBody();
    Expression _expression = _body.getExpression();
    final Expression mBodyExp = EcoreUtil.<Expression>copy(_expression);
    EList<Parameter> _params = m.getParams();
    Expression _main = p.getMain();
    EList<Expression> _args = ((Selection) _main).getArgs();
    this._fjSemanticsUtils.replaceParams(mBodyExp, _params, _args);
    String _string = expected.toString();
    String _string_1 = this.stringRep.string(mBodyExp);
    Assert.assertEquals(_string, _string_1);
  }
  
  private void assertThisAndParamsReplacement(final CharSequence prog, final CharSequence expected) {
    final Program p = this.parseAndAssertNoError(prog);
    final Method m = this.methodByName(p, "m");
    MethodBody _body = m.getBody();
    Expression _expression = _body.getExpression();
    final Expression mBodyExp = EcoreUtil.<Expression>copy(_expression);
    Expression _main = p.getMain();
    Expression _receiver = ((Selection) _main).getReceiver();
    EList<Parameter> _params = m.getParams();
    Expression _main_1 = p.getMain();
    EList<Expression> _args = ((Selection) _main_1).getArgs();
    this._fjSemanticsUtils.replaceThisAndParams(mBodyExp, _receiver, _params, _args);
    String _string = expected.toString();
    String _string_1 = this.stringRep.string(mBodyExp);
    Assert.assertEquals(_string, _string_1);
  }
  
  private void assertReduceAll(final CharSequence prog, final CharSequence expectedTrace) {
    Program _parseAndAssertNoError = this.parseAndAssertNoError(prog);
    Expression _main = _parseAndAssertNoError.getMain();
    Expression exp = EcoreUtil.<Expression>copy(_main);
    Result<Expression> result = this.assertReduce(exp);
    Expression _value = result.getValue();
    boolean _isValue = this._fjSemanticsUtils.isValue(_value);
    boolean _not = (!_isValue);
    boolean _while = _not;
    while (_while) {
      {
        Expression _value_1 = result.getValue();
        exp = _value_1;
        Result<Expression> _assertReduce = this.assertReduce(exp);
        result = _assertReduce;
      }
      Expression _value_1 = result.getValue();
      boolean _isValue_1 = this._fjSemanticsUtils.isValue(_value_1);
      boolean _not_1 = (!_isValue_1);
      _while = _not_1;
    }
    String _string = expectedTrace.toString();
    String _trim = _string.trim();
    String _traceAsString = this.traceUtils.traceAsString(this.trace);
    Assert.assertEquals(_trim, _traceAsString);
  }
  
  private Expression assertReduceOneStep(final CharSequence prog, final CharSequence expected, final CharSequence expectedTrace) {
    Program _parseAndAssertNoError = this.parseAndAssertNoError(prog);
    Expression _main = _parseAndAssertNoError.getMain();
    Expression _copy = EcoreUtil.<Expression>copy(_main);
    Expression _assertReduceOneStep = this.assertReduceOneStep(_copy, expected, expectedTrace);
    return _assertReduceOneStep;
  }
  
  private Expression assertReduceOneStep(final Expression exp, final CharSequence expected, final CharSequence expectedTrace) {
    Expression _xblockexpression = null;
    {
      final Result<Expression> result = this.assertReduce(exp);
      boolean _notEquals = (!Objects.equal(expected, null));
      if (_notEquals) {
        String _string = expected.toString();
        Expression _value = result.getValue();
        String _string_1 = this.stringRep.string(_value);
        Assert.assertEquals(_string, _string_1);
      }
      boolean _notEquals_1 = (!Objects.equal(expectedTrace, null));
      if (_notEquals_1) {
        String _string_2 = expectedTrace.toString();
        String _traceAsString = this.traceUtils.traceAsString(this.trace);
        Assert.assertEquals(_string_2, _traceAsString);
      }
      Expression _value_1 = result.getValue();
      _xblockexpression = (_value_1);
    }
    return _xblockexpression;
  }
  
  private Result<Expression> assertReduce(final Expression exp) {
    Result<Expression> _xblockexpression = null;
    {
      final Result<Expression> result = this.fjSystem.reduce(null, this.trace, exp);
      boolean _failed = result.failed();
      if (_failed) {
        RuleFailedException _ruleFailedException = result.getRuleFailedException();
        String _failureTraceAsString = this.traceUtils.failureTraceAsString(_ruleFailedException);
        InputOutput.<String>println(_failureTraceAsString);
        Assert.fail();
      }
      _xblockexpression = (result);
    }
    return _xblockexpression;
  }
  
  private void assertReduceWrong(final CharSequence prog, final CharSequence expectedTrace) {
    Program _parseAndAssertNoError = this.parseAndAssertNoError(prog);
    Expression _main = _parseAndAssertNoError.getMain();
    final Expression exp = EcoreUtil.<Expression>copy(_main);
    final Result<Expression> result = this.fjSystem.reduce(null, this.trace, exp);
    boolean _failed = result.failed();
    if (_failed) {
      String _string = expectedTrace.toString();
      RuleFailedException _ruleFailedException = result.getRuleFailedException();
      String _failureTraceAsString = this.traceUtils.failureTraceAsString(_ruleFailedException);
      Assert.assertEquals(_string, _failureTraceAsString);
    } else {
      Expression _value = result.getValue();
      String _string_1 = this.stringRep.string(_value);
      String _plus = ("unexpected success: " + _string_1);
      Assert.fail(_plus);
    }
  }
  
  private void assertSubstitutionLemma(final CharSequence prog, final CharSequence expectedTrace) {
    final Program p = this.parseAndAssertNoError(prog);
    final Method m = this.methodByName(p, "m");
    this.trace.addToTrace("WELLTYPED METHOD BODY");
    it.xsemantics.example.fj.fj.Class _containerOfType = EcoreUtil2.<it.xsemantics.example.fj.fj.Class>getContainerOfType(m, it.xsemantics.example.fj.fj.Class.class);
    final ClassType typeForThis = FjTypeUtils.createClassType(_containerOfType);
    RuleEnvironment _environmentEntry = this.fjSystem.environmentEntry("this", typeForThis);
    RuleEnvironment _ruleEnvironment = new RuleEnvironment(_environmentEntry);
    MethodBody _body = m.getBody();
    Expression _expression = _body.getExpression();
    final Result<Type> methodBodyType = this.fjSystem.type(_ruleEnvironment, 
      this.trace, _expression);
    this.<Type>assertResult(methodBodyType);
    MethodBody _body_1 = m.getBody();
    final MethodBody mBody = EcoreUtil.<MethodBody>copy(_body_1);
    Expression _expression_1 = mBody.getExpression();
    Expression _main = p.getMain();
    Expression _receiver = ((Selection) _main).getReceiver();
    EList<Parameter> _params = m.getParams();
    Expression _main_1 = p.getMain();
    EList<Expression> _args = ((Selection) _main_1).getArgs();
    this._fjSemanticsUtils.replaceThisAndParams(_expression_1, _receiver, _params, _args);
    this.trace.addToTrace("WELLTYPED AFTER SUBSTITUTION");
    Expression _expression_2 = mBody.getExpression();
    final Result<Type> substType = this.fjSystem.type(null, this.trace, _expression_2);
    this.<Type>assertResult(substType);
    this.trace.addToTrace("SUBTYPE AFTER SUBSTITUTION");
    Type _value = substType.getValue();
    Type _value_1 = methodBodyType.getValue();
    final Result<Boolean> isSubtype = this.fjSystem.subtype(null, this.trace, _value, _value_1);
    this.<Boolean>assertResult(isSubtype);
    String _string = expectedTrace.toString();
    String _traceAsString = this.traceUtils.traceAsString(this.trace);
    Assert.assertEquals(_string, _traceAsString);
  }
  
  private <T extends Object> void assertResult(final Result<T> result) {
    boolean _failed = result.failed();
    if (_failed) {
      RuleFailedException _ruleFailedException = result.getRuleFailedException();
      String _failureTraceAsString = this.traceUtils.failureTraceAsString(_ruleFailedException);
      InputOutput.<String>println(_failureTraceAsString);
      Assert.fail();
    }
  }
}

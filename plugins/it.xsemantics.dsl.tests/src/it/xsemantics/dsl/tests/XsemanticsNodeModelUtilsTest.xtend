package it.xsemantics.dsl.tests

import com.google.inject.Inject
import it.xsemantics.dsl.XsemanticsInjectorProvider
import it.xsemantics.dsl.util.XsemanticsUtils
import it.xsemantics.dsl.util.XsemanticsNodeModelUtils
import org.junit.Assert
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.emf.ecore.EcoreFactory

@InjectWith(typeof(XsemanticsInjectorProvider))
@RunWith(typeof(XtextRunner))
class XsemanticsNodeModelUtilsTest extends XsemanticsBaseTest {
	
	@Inject extension XsemanticsUtils
	
	@Inject XsemanticsNodeModelUtils nodeModelUtils
	
	@Test
	def void testRuleInvocationString() {
		var ts = testFiles.testRuleInvokingAnotherRule.parseAndAssertNoError
		val ruleInvocations = ts.ruleInvocations
		Assert::assertEquals(2, ruleInvocations.size)
		Assert::assertEquals("G |- object.eClass : eClass",
			nodeModelUtils.getProgramText(ruleInvocations.get(0)))
		Assert::assertEquals("G |- eClass : object.eClass", 
			nodeModelUtils.getProgramText(ruleInvocations.get(1)))
	}
	
	@Test
	def void testNoNodeForObject() {
		val eClass = EcoreFactory::eINSTANCE.createEClass
		Assert::assertTrue(nodeModelUtils.getProgramText(eClass) == null)
	}
}
package com.evgenii.aescrypto.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;
import android.webkit.WebView;

import com.evgenii.aescrypto.JsRunner;
import com.evgenii.aescrypto.exceptions.InitialJsHasAlreadyBeenRun;
import com.evgenii.aescrypto.interfaces.JsCallback;
import com.evgenii.aescrypto.test.mocks.JsCallbackMock;

public class JsRunnerTest extends AndroidTestCase {
	protected JsRunner mJsRunner;

	@Override
	protected void setUp() {
		mJsRunner = new JsRunner(mContext);
	}

	public void testRun() {
		assertTrue(true);
	}

	public void testGetWebView_shouldCreateWebView() {
		final WebView webView = mJsRunner.getWebView();
		assertNotNull(webView);
	}

	public void testAddInitialJs() throws InitialJsHasAlreadyBeenRun {
		mJsRunner.addInitialJs("function first(){};");
		mJsRunner.addInitialJs("function second(){};");
		assertEquals(" function first(){}; function second(){};",
				mJsRunner.getInitialJSConcatenated());
	}

	public void testGetCompleteInitialJsToEvaluate()
			throws InitialJsHasAlreadyBeenRun {
		mJsRunner.addInitialJs("function first(){};");
		mJsRunner.addInitialJs("function second(){};");
		assertEquals(
				"javascript:  function first(){}; function second(){}; AESCrypto.initialJsExecuted(); ",
				mJsRunner.getCompleteInitialJsToEvaluate());
	}

	public void testAddInitialJs_canNotAddAfterRunning() {
		try {
			mJsRunner.runInitialJs();
			mJsRunner.addInitialJs("function second(){};");
		} catch (final InitialJsHasAlreadyBeenRun e) {
		}
	}

	public void testInitialJsEvaluationShouldNotBeStartedByDefault() {
		assertFalse(mJsRunner.getInitialJsEvaluationStarted());
	}

	public void testRunInitialJs_startInitialJsEvaluation() {
		mJsRunner.runInitialJs();
		assertTrue(mJsRunner.getInitialJsEvaluationStarted());
	}

	public void testInitialJsEvaluationShouldNotBeFinishedByDefault() {
		assertFalse(mJsRunner.getInitialJsEvaluationFinished());
	}

	public void testInitalJsEvaluationHasFinished_markInitialJsAsFinished() {
		mJsRunner.initalJsEvaluationHasFinished();
		assertTrue(mJsRunner.getInitialJsEvaluationFinished());
	}

	public void testInitalJsEvaluationHasFinished_executesPendingJs() {
		mJsRunner.runJsFunction("myFirstName", "param1");
		mJsRunner.runJsFunction("mySecondName", "param2");
		mJsRunner.initalJsEvaluationHasFinished();
		assertEquals(0, mJsRunner.getPendingJsCalls().size());
	}

	public void testRunJsFunction() {
		mJsRunner.runJsFunction("myFirstName", "param1");
		mJsRunner.runJsFunction("mySecondName", "param2");
		assertEquals(2, mJsRunner.getPendingJsCalls().size());
	}

	public void testExecuteAllPendingJs() {
		mJsRunner.executeAllPendingJs();
	}

	public void testGetJsForFunctionCall() {
		assertEquals("javascript: AESCrypto.result(drink('milk'));",
				JsRunner.getJsForFunctionCall("drink('milk')"));
	}

	public void testJsCallFinished_runsCallback() {
		final ArrayList<JsCallback> callbacks = mJsRunner.getJsCallbacks();
		final JsCallbackMock callback = new JsCallbackMock();
		callbacks.add(callback);

		mJsRunner.jsCallFinished("my result", 0);
		assertEquals("my result", callback.resultValue);
	}
}

package com.wirecard.accept.uicomponents.signature;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.IntRange;
import android.view.MotionEvent;
import android.widget.Button;

import com.wirecard.accept.uicomponents.BuildConfig;
import com.wirecard.accept.uicomponents.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.FragmentTestUtil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SignatureFragmentDialogTests {

    @Test(expected = NullPointerException.class)
    public void signatureConfirmCallbackNotSet() {
        SignatureFragmentDialog signatureFragment = SignatureFragmentDialog.newInstance(null, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
    }

    @Test(expected = NullPointerException.class)
    public void signatureCancelCallbackNotSet() {
        SignatureFragmentDialog signatureFragment = SignatureFragmentDialog.newInstance(null, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button cancel = (Button) signatureFragment.getView().findViewById(R.id.button_cancel);
        cancel.performClick();
    }

    @Test
    public void signatureNotFinished() {
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureFragmentDialog signatureFragment = SignatureFragmentDialog.newInstance(signatureContract, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        verify(signatureContract, times(1)).onNotSigned();
    }

    @Test
    public void signatureFinished() {
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureFragmentDialog signatureFragment = SignatureFragmentDialog.newInstance(signatureContract, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        SignatureView signatureView = (SignatureView) signatureFragment.getView().findViewById(R.id.signature_view);
        //simulate drawing
        signatureView.onSizeChanged(1, 1, 0, 0);
        signatureView.onTouchEvent(MotionEvent.obtain(1L, 1L, 1, 1f, 1f, 1));
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        verify(signatureContract, times(1)).onSignatureFinished(any(byte[].class));
    }

    @Test
    public void signatureConfirmation() {
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureConfirmContract signatureConfirmContract = mock(SignatureConfirmContract.class);
        SignatureFragmentDialog signatureFragment = SignatureFragmentDialog.newInstance(signatureContract, signatureConfirmContract);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        SignatureView signatureView = (SignatureView) signatureFragment.getView().findViewById(R.id.signature_view);
        //simulate drawing
        signatureView.onSizeChanged(1, 1, 0, 0);
        signatureView.onTouchEvent(MotionEvent.obtain(1L, 1L, 1, 1f, 1f, 1));
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        confirm.performClick();
        verify(signatureConfirmContract, times(1)).onSignatureConfirm();
    }

    @Test
    public void signatureCancel() {
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureFragmentDialog signatureFragment = SignatureFragmentDialog.newInstance(signatureContract, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button cancel = (Button) signatureFragment.getView().findViewById(R.id.button_cancel);
        cancel.performClick();
        verify(signatureContract, times(1)).onSignatureCancel();
    }

    @Test
    public void signatureDecline() {
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureConfirmContract signatureConfirmContract = mock(SignatureConfirmContract.class);
        SignatureFragmentDialog signatureFragment = SignatureFragmentDialog.newInstance(signatureContract, signatureConfirmContract);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        SignatureView signatureView = (SignatureView) signatureFragment.getView().findViewById(R.id.signature_view);
        //simulate drawing
        signatureView.onSizeChanged(1, 1, 0, 0);
        signatureView.onTouchEvent(MotionEvent.obtain(1L, 1L, 1, 1f, 1f, 1));
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        Button cancel = (Button) signatureFragment.getView().findViewById(R.id.button_cancel);
        cancel.performClick();
        verify(signatureConfirmContract, times(1)).onSignatureDecline();
    }

    @Test
    public void showAsDialog() {
        SignatureFragmentDialog signatureFragmentDialog = SignatureFragmentDialog.newInstance(null, null);
        Activity activity = Robolectric.buildActivity(Activity.class).create()
                .start()
                .resume()
                .visible()
                .get();

        signatureFragmentDialog.show(activity.getFragmentManager(), "dialog");

        Dialog dialog = ShadowDialog.getLatestDialog();
        assertNull(dialog);
    }

}

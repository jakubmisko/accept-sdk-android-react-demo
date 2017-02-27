package com.wirecard.accept.uicomponents.signature;

import android.view.MotionEvent;
import android.widget.Button;

import com.wirecard.accept.uicomponents.BuildConfig;
import com.wirecard.accept.uicomponents.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.util.FragmentTestUtil;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SignatureFragmentTests {

    @Test(expected = NullPointerException.class)
    public void signatureConfirmCallbackNotSet(){
        SignatureFragment signatureFragment = SignatureFragment.newInstance(null, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
    }

    @Test(expected = NullPointerException.class)
    public void signatureCancelCallbackNotSet(){
        SignatureFragment signatureFragment = SignatureFragment.newInstance(null, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button cancel = (Button) signatureFragment.getView().findViewById(R.id.button_cancel);
        cancel.performClick();
    }

    @Test
    public void signatureNotFinished(){
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureFragment signatureFragment = SignatureFragment.newInstance(signatureContract, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        verify(signatureContract, times(1)).onNotSigned();
    }

    @Test
    public void signatureFinished(){
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureFragment signatureFragment = SignatureFragment.newInstance(signatureContract, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        SignatureView signatureView = (SignatureView) signatureFragment.getView().findViewById(R.id.signature_view);
        //simulate drawing
        signatureView.onSizeChanged(1,1,0,0);
        signatureView.onTouchEvent(MotionEvent.obtain(1L,1L,1,1f,1f, 1));
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        verify(signatureContract, times(1)).onSignatureFinished(any(byte[].class));
    }

    @Test
    public void signatureConfirmation()  {
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureConfirmContract signatureConfirmContract = mock(SignatureConfirmContract.class);
        SignatureFragment signatureFragment = SignatureFragment.newInstance(signatureContract, signatureConfirmContract);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        SignatureView signatureView = (SignatureView) signatureFragment.getView().findViewById(R.id.signature_view);
        //simulate drawing
        signatureView.onSizeChanged(1,1,0,0);
        signatureView.onTouchEvent(MotionEvent.obtain(1L,1L,1,1f,1f, 1));
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        confirm.performClick();
        verify(signatureConfirmContract, times(1)).onSignatureConfirm();
    }

    @Test
    public void signatureCancel(){
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureFragment signatureFragment = SignatureFragment.newInstance(signatureContract, null);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        Button cancel = (Button) signatureFragment.getView().findViewById(R.id.button_cancel);
        cancel.performClick();
        verify(signatureContract, times(1)).onSignatureCancel();
    }

    @Test
    public void signatureDecline()  {
        SignatureContract signatureContract = mock(SignatureContract.class);
        SignatureConfirmContract signatureConfirmContract = mock(SignatureConfirmContract.class);
        SignatureFragment signatureFragment = SignatureFragment.newInstance(signatureContract, signatureConfirmContract);
        FragmentTestUtil.startFragment(signatureFragment);
        assertNotNull(signatureFragment);
        SignatureView signatureView = (SignatureView) signatureFragment.getView().findViewById(R.id.signature_view);
        //simulate drawing
        signatureView.onSizeChanged(1,1,0,0);
        signatureView.onTouchEvent(MotionEvent.obtain(1L,1L,1,1f,1f, 1));
        Button confirm = (Button) signatureFragment.getView().findViewById(R.id.button_confirm);
        confirm.performClick();
        Button cancel = (Button) signatureFragment.getView().findViewById(R.id.button_cancel);
        cancel.performClick();
        verify(signatureConfirmContract, times(1)).onSignatureDecline();
    }

}

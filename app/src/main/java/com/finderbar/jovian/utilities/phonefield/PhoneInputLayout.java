package com.finderbar.jovian.utilities.phonefield;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import com.finderbar.jovian.R;

/**
 * Created by thein on 12/7/18.
 */

public class PhoneInputLayout extends PhoneField {

    private TextInputLayout mTextInputLayout;

    public PhoneInputLayout(Context context) {
        this(context, null);
    }

    public PhoneInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoneInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void updateLayoutAttributes() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.TOP);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void prepareView() {
        super.prepareView();
        mTextInputLayout = (TextInputLayout) findViewWithTag(getResources().getString(R.string.phonefield_til_phone));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_phone_layout;
    }

    @Override
    public void setHint(int resId) {
        mTextInputLayout.setHint(getContext().getString(resId));
    }

    @Override
    public void setError(String error) {
        if (error == null || error.length() == 0) {
            mTextInputLayout.setErrorEnabled(false);
        } else {
            mTextInputLayout.setErrorEnabled(true);
        }
        mTextInputLayout.setError(error);
    }

    public TextInputLayout getTextInputLayout() {
        return mTextInputLayout;
    }
}

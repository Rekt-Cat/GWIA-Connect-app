package com.maid.gwiaconnect;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.cancel.OnCancel;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.error.ErrorInfo;
import com.paypal.checkout.error.OnError;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;
import com.paypal.checkout.shipping.OnShippingChange;
import com.paypal.checkout.shipping.ShippingChangeActions;
import com.paypal.checkout.shipping.ShippingChangeData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Payment_Fragment extends Fragment {
    String amount;
    TextInputLayout textInputLayout;
    PayPalButton pay;
    public static final String CLIENT_ID = "AT7gpN6DFC9WiNeUQJUSsTaq-fsuF98CkNr_vg15OWKe5s5fXMZW7USCH4aB7d6AN9gMCj1xF0sTCMz4";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PayPalCheckout.setConfig(new CheckoutConfig(
                getActivity().getApplication(),
                CLIENT_ID,
                Environment.SANDBOX,
                CurrencyCode.USD, UserAction.PAY_NOW,
                "com.maid.gwiaconnect://paypalpay"));
        return inflater.inflate(R.layout.fragment_payment_, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // payPal=view.findViewById(R.id.paypal);
        pay = view.findViewById(R.id.payment_button_container);
        textInputLayout = view.findViewById(R.id.amount);

        Log.d("woosh", "onViewCreated: "+amount);

        pay.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        if(textInputLayout.getEditText().getText().toString().equals("")){
                            textInputLayout.setError("Please enter an amount.");
                        }
                        else{
                            textInputLayout.setError(null);
                        }
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        Log.d("jj", "created");
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(textInputLayout.getEditText().getText().toString())
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );

                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                        Log.d("jj", "order created");
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        Log.d("jj", "approved");
                        Date currentTime = Calendar.getInstance().getTime();
                        String data=textInputLayout.getEditText().getText().toString();

                        FirebaseDatabase.getInstance().getReference("Transactions")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(currentTime.toString()).setValue(data);

                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                            }
                        });
                    }
                },
                new OnShippingChange() {

                    @Override
                    public void onShippingChanged(@NonNull ShippingChangeData shippingChangeData, @NonNull ShippingChangeActions shippingChangeActions) {

                    }
                },
                new OnCancel() {
                    @Override
                    public void onCancel() {
                        Log.d("jj", "Buyer cancelled the PayPal experience.");
                    }
                },
                new OnError() {
                    @Override
                    public void onError(@NotNull ErrorInfo errorInfo) {
                        Log.d("jj", String.format("Error: %s", errorInfo));
                    }
                }
        );
    }


}



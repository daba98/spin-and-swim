package de.die_bartmanns.spinnandfly.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.die_bartmanns.spinnandfly.BillingSecurity;
import de.die_bartmanns.spinnandfly.Data;
import de.die_bartmanns.spinnandfly.R;

/**
 * Created by Daniel on 26.04.2018.
 */
public class BuyShellsActivity extends Activity implements BillingClientStateListener{

    private final String BUY_100_SHELLS = "shells_100";
    private final String BUY_500_SHELLS = "shells_500";
    private final String BUY_1000_SHELLS = "shells_1000";
    private final String BUY_5000_SHELLS = "shells_5000";

    private final String TAG = "Billing";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final long RECONNECT_TIMER_START_MILLISECONDS = 1000L;
    private static final long RECONNECT_TIMER_MAX_TIME_MILLISECONDS = 1000L * 60 * 2;
    private long reconnectMilliseconds = RECONNECT_TIMER_START_MILLISECONDS;

    private TextView shellTextView;
    private TextView priceOneShell;
    private TextView priceFewShells;
    private TextView priceSeveralShells;
    private TextView priceManyShells;

    private Data data;
    private Map<String, SkuDetails> productMap;

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.buy_shells);

        init();
    }

    private void init(){
        productMap = new HashMap<>();
        data = Data.getMyData(getApplicationContext());
        initUI();
        // Handle an error caused by a user cancelling the purchase flow.
        // Handle any other error codes.
        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                            if (!isSignatureValid(purchase)) {
                                Log.e(TAG, "Invalid signature on purchase. Check to make sure your public key is correct.");
                                continue;
                            }
                            handlePurchase(purchase);
                        }
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
            }
        };

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(this);
    }

    private void queryOneTimeProducts() {
        List<String> skuQueryList = new ArrayList<>();
        skuQueryList.add(BUY_100_SHELLS);
        skuQueryList.add(BUY_500_SHELLS);
        skuQueryList.add(BUY_1000_SHELLS);
        skuQueryList.add(BUY_5000_SHELLS);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuQueryList).setType(BillingClient.SkuType.INAPP);
        // SkuType.INAPP refers to 'managed products' or one time purchases.
        // To query for subscription products, you would use SkuType.SUBS.

        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        Log.i(TAG, "onSkuDetailsResponse ${result?.responseCode}");
                        if (skuDetailsList != null) {
                            productMap.clear();
                            for (SkuDetails skuDetails : skuDetailsList) {
                                productMap.put(skuDetails.getSku(), skuDetails);
                                Log.i(TAG, skuDetails.toString());
                            }
                            setPrices();
                        } else {
                            Log.i(TAG, "No skus found from query");
                        }
                    }
                });
        }

    private void launchPurchaseFlow(SkuDetails skuDetails) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        BillingResult responseCode = billingClient.launchBillingFlow(this, flowParams);
        Log.i(TAG, "launchPurchaseFlow result " + responseCode.toString());
    }

    private void handlePurchase(Purchase purchase){
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken()).build();
        final Purchase currentPurchase = purchase;

        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    Log.i(TAG, "PurchaseToken " + purchaseToken);
                    int boughtShells = 0;
                    if(currentPurchase.getSku().equals(BUY_100_SHELLS))
                        boughtShells += 100;
                    else if(currentPurchase.getSku().equals(BUY_500_SHELLS))
                        boughtShells += 500;
                    else if(currentPurchase.getSku().equals(BUY_1000_SHELLS))
                        boughtShells += 1000;
                    else if(currentPurchase.getSku().equals(BUY_5000_SHELLS))
                        boughtShells += 5000;
                    data.shells += boughtShells;
                    data.storeData(getBaseContext());
                    updateShellView(boughtShells);
                }
            }
        });
    }

    private void initUI(){
        Util.hideBottomNavigationBar(this);
        Util.disableDarkMode(this);
        shellTextView = (TextView) findViewById(R.id.shellTextView);
        shellTextView.setText(String.valueOf(data.shells));
        priceOneShell = findViewById(R.id.price_one_shell);
        priceFewShells = findViewById(R.id.price_few_shells);
        priceSeveralShells = findViewById(R.id.price_several_shells);
        priceManyShells = findViewById(R.id.price_many_shells);
    }

    private void setPrices(){
        if(productMap.get(BUY_100_SHELLS) != null)
            priceOneShell.setText(productMap.get(BUY_100_SHELLS).getPrice());
        if(productMap.get(BUY_500_SHELLS) != null)
            priceFewShells.setText(productMap.get(BUY_500_SHELLS).getPrice());
        if(productMap.get(BUY_1000_SHELLS) != null)
            priceSeveralShells.setText(productMap.get(BUY_1000_SHELLS).getPrice());
        if(productMap.get(BUY_5000_SHELLS) != null)
            priceManyShells.setText(productMap.get(BUY_5000_SHELLS).getPrice());
    }

    private void updateShellView(int boughtShells){
        ValueAnimator countAnimator = new ValueAnimator();
        countAnimator.setObjectValues(data.shells - boughtShells, data.shells);
        countAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                shellTextView.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        countAnimator.setDuration(1500);
        countAnimator.start();
    }

    private boolean isSignatureValid(Purchase purchase) {
        return BillingSecurity.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature());
    }

    public void clickedOneShell(View view){
        if(productMap.get(BUY_100_SHELLS) != null)
            launchPurchaseFlow(productMap.get(BUY_100_SHELLS));
    }

    public void clickedFewShells(View view){
        if(productMap.get(BUY_500_SHELLS) != null)
            launchPurchaseFlow(productMap.get(BUY_500_SHELLS));
    }

    public void clickedSeveralShells(View view){
        if(productMap.get(BUY_1000_SHELLS) != null)
            launchPurchaseFlow(productMap.get(BUY_1000_SHELLS));
    }

    public void clickedManyShells(View view){
        if(productMap.get(BUY_5000_SHELLS) != null)
            launchPurchaseFlow(productMap.get(BUY_5000_SHELLS));
    }

    public void clickedBack(View view){
        onBackPressed();
    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
            // The BillingClient is ready. You can query purchases here.
            Log.i(TAG, "Billing client successfully set up");
            queryOneTimeProducts();
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        Log.i(TAG, "Billing service disconnected");
        // Restart the connection with startConnection() so future requests don't fail.
        retryBillingServiceConnectionWithExponentialBackoff();
    }

    private void retryBillingServiceConnectionWithExponentialBackoff() {
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    billingClient.startConnection(BuyShellsActivity.this);
                                }
                            },
                reconnectMilliseconds);
        reconnectMilliseconds = Math.min(reconnectMilliseconds * 2, RECONNECT_TIMER_MAX_TIME_MILLISECONDS);
    }
}

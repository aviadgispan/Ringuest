package com.ringchash.dodot.aviad.ringuest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by AVIAD on 1/11/2015.
 * this activity is for the bill page
 */
public class Bill extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill);
        ImageButton backButton=(ImageButton)findViewById(R.id.back);
        Button btnGetMyMoney=(Button)findViewById(R.id.get_money_now);
        int[] arrOfTextId={R.id.header_account_status,R.id.cash_now_in_bill_header,R.id.cash_now_in_bill,R.id.shekel,R.id.shekel2,
        R.id.sum_if_all_money_until_today,R.id.money_untill_now};
        // put font..
        Typeface tfAlef;
        tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        int counterRing=sp.getInt(ConfigAppData.COUNTER_ALL_RING,0);
        int counterRingUnPaid=sp.getInt(ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID,0);
        int takbulRing=sp.getInt(ConfigAppData.TAKBUL_ALL_RING,0);
        int takbulUnPaid=sp.getInt(ConfigAppData.TAKBUL_ALL_RING_THAT_UNPAID,0);

        double sumOfAllMoneyFromRing=(double)takbulRing/100;
        double sumOfAllMoneyFromRingUnPaid=(double)takbulUnPaid/100;

//        double sumOfAllMoneyFromRing=counterRing*ConfigAppData.PAY_FOR_RING;
//        double sumOfAllMoneyFromRingUnPaid=counterRingUnPaid*ConfigAppData.PAY_FOR_RING;

/**
 * fix the string to be only 2 chars after the point
 */
        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing*100;
        sumOfAllMoneyFromRing=Math.round(sumOfAllMoneyFromRing);
        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing/100;
/**
 * fix the string to be only 2 chars after the point
 */
        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing*100;
        sumOfAllMoneyFromRing=Math.round(sumOfAllMoneyFromRing);
        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing/100;

/**
 * update font in the page of activity
 */
        for(int i=0;i<arrOfTextId.length;i++){
            TextView t=(TextView)findViewById(arrOfTextId[i]);
            t.setTypeface(tfAlef);
        }
        TextView moneyUnPaid=(TextView)findViewById(R.id.shekel);
        moneyUnPaid.setText(" "+sumOfAllMoneyFromRingUnPaid);
        TextView moneyPaid=(TextView)findViewById(R.id.sum_if_all_money_until_today);

        moneyPaid.setText(" "+sumOfAllMoneyFromRing);
        //asking for money...
        btnGetMyMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.CASH_IN");
                startActivity(intent);

            }

            ;
        });
        //back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                startActivity(intent);

            };
        });

    }
}

package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by AVIAD on 1/11/2015.
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
        Typeface tfAlef;
        tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        int counterRing=sp.getInt(ConfigAppData.COUNTER_ALL_RING,0);
        int counterRingUnPaid=sp.getInt(ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID,0);

        double sumOfAllMoneyFromRing=counterRing*ConfigAppData.PAY_FOR_RING;

        double sumOfAllMoneyFromRingUnPaid=counterRingUnPaid*ConfigAppData.PAY_FOR_RING;
        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing*100;
        sumOfAllMoneyFromRing=Math.round(sumOfAllMoneyFromRing);
        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing/100;

        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing*100;
        sumOfAllMoneyFromRing=Math.round(sumOfAllMoneyFromRing);
        sumOfAllMoneyFromRing=sumOfAllMoneyFromRing/100;
//        double val = ....;
//        val = val*100;
//        val = Math.Round(val);
//        val = val /100;
        for(int i=0;i<arrOfTextId.length;i++){
            TextView t=(TextView)findViewById(arrOfTextId[i]);
            t.setTypeface(tfAlef);
        }
        TextView moneyUnPaid=(TextView)findViewById(R.id.shekel);
        moneyUnPaid.setText(" "+sumOfAllMoneyFromRingUnPaid);
        TextView moneyPaid=(TextView)findViewById(R.id.sum_if_all_money_until_today);

        moneyPaid.setText(" "+sumOfAllMoneyFromRing);
        btnGetMyMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.CASH_IN");
                startActivity(intent);

            }

            ;
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                startActivity(intent);

            };
        });

    }
}

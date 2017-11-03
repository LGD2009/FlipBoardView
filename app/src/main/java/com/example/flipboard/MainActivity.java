package com.example.flipboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private FlipBoardView flipBoardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        flipBoardView= (FlipBoardView) findViewById(R.id.flip);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipBoardView.start();
            }
        });
    }
}

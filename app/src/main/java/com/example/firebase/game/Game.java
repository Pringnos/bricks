package com.example.firebase.game;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.firebase.game.BoardGame;
import com.example.firebase.R;

public class Game extends AppCompatActivity {
    BoardGame boardGame;
    int levelNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        levelNumber = getIntent().getIntExtra("LEVEL_NUMBER", 1);

        boardGame = new BoardGame(this, levelNumber);

        FrameLayout framelayout =(FrameLayout)findViewById(R.id.MFRM);
        framelayout.addView(boardGame);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (boardGame != null) {
            boardGame.destroy();  // Clean up game resources
        }
    }

}
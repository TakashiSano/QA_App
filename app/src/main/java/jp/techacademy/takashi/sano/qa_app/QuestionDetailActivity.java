package jp.techacademy.takashi.sano.qa_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;

    //佐野が追加した
    private Switch mSwitch = null;
    SharedPreferences mPreference;
    //佐野が追加した


    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    // 佐野が追加した
    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_question_detail);

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // ログインしていなければswitchを消す
            setContentView(R.layout.activity_question_detail);
            findViewById(R.id.switch1).setVisibility(View.INVISIBLE);
        } else {
            // ログインしていればswitchを表示
            setContentView(R.layout.activity_question_detail);
            findViewById(R.id.switch1).setVisibility(View.VISIBLE);
        }

        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを収録する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                }
            }
        });

        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

        mSwitch = (Switch)findViewById(R.id.switch1);

        //Switch保持のために追加
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        //Switch保持のために追加

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                
                if(mSwitch.isChecked()) {
                    Log.v("スイッチ", "ON");
                    //Switch保持のために追加
                    SharedPreferences.Editor editor = mPreference.edit();
                    editor.putBoolean("スイッチ",true);
                    editor.commit();
                    //Switch保持のために追加
                    Toast.makeText(QuestionDetailActivity.this, "お気に入りに追加しました。", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("スイッチ", "OFF");
                    //Switch保持のために追加
                    SharedPreferences.Editor editor = mPreference.edit();
                    editor.putBoolean("スイッチ",false);
                    editor.commit();
                    //Switch保持のために追加
                    Toast.makeText(QuestionDetailActivity.this, "お気に入りから削除しました。", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //佐野が追加した

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        //Switch保持のために追加
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        //Switch保持のために追加

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        // 佐野が追加した
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // ログインしていなければswitchを消す
            setContentView(R.layout.activity_question_detail);
            findViewById(R.id.switch1).setVisibility(View.INVISIBLE);
        } else {
            // ログインしていればswitchを表示
            setContentView(R.layout.activity_question_detail);
            findViewById(R.id.switch1).setVisibility(View.VISIBLE);
        }
        // 佐野が追加した



        // ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ログイン済みのユーザーを収録する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    // Questionを渡して回答作成画面を起動する
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                }
            }
        });

        DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

        //佐野が追加した
        mSwitch = (Switch)findViewById(R.id.switch1);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(mSwitch.isChecked()) {
                    Log.v("スイッチ", "ON");
                    //Switch保持のために追加
                    SharedPreferences.Editor editor = mPreference.edit();
                    editor.putBoolean("SWITCH" ,true);
                    editor.commit();
                    //Switch保持のために追加
                    Toast.makeText(QuestionDetailActivity.this, "お気に入りに追加しました。", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("スイッチ", "OFF");
                    //Switch保持のために追加
                    SharedPreferences.Editor editor = mPreference.edit();
                    editor.putBoolean("SWITCH",false);
                    editor.commit();
                    //Switch保持のために追加
                    Toast.makeText(QuestionDetailActivity.this, "お気に入りから削除しました。", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //佐野が追加した

    }
}

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
import com.google.gson.Gson;

import java.util.HashMap;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;
    private DatabaseReference mAnswerRef;

    //佐野が追加した
    private Switch mSwitch;
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

        //Switch保持のために追加
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        //Switch保持のために追加

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        mSwitch = (Switch) findViewById(R.id.switch1);

         /* ******************************************************************/
        // Preferenceから登録されている対象データを取得する。
        String question = mPreference.getString( mQuestion.getQuestionUid(), "" );
        // データが存在している場合
        if( !question.equals(""))
        {
            // Questionクラスに戻す。
            Gson gson = new Gson();
            mQuestion = gson.fromJson( question, Question.class );
        }

        // 最初は必ずNULLなので、チェックを行う。
        if( mQuestion.getStar_flag() == null )
        {
            // NULLの場合には、必ずfalseを入れる。（初期値）
            mQuestion.setStar_flag( false );
        }

        // お気に入りの状態を設定
        mSwitch.setChecked(mQuestion.getStar_flag());
        /* ******************************************************************/


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // ログインしていなければswitchを消す
            mSwitch.setVisibility(View.INVISIBLE);
        } else {
            // ログインしていればswitchを表示
            mSwitch.setVisibility(View.VISIBLE);
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
                    Log.v("uid", "ON");
                    //Switch保持のために追加
                    SharedPreferences.Editor editor = mPreference.edit();
                    Gson gson = new Gson();
                    editor.putString(mQuestion.getQuestionUid() ,gson.toJson(mQuestion));
                    editor.commit();
                    //Switch保持のために追加
                    Toast.makeText(QuestionDetailActivity.this, "お気に入りに追加しました。", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("uid", "OFF");
                    //Switch保持のために追加
                    SharedPreferences.Editor editor = mPreference.edit();
                    Gson gson = new Gson();
                    editor.putString(mQuestion.getQuestionUid() ,gson.toJson(mQuestion));
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

        mSwitch = (Switch) findViewById(R.id.switch1);

        /* ******************************************************************/
        // Preferenceから登録されている対象データを取得する。
        String question = mPreference.getString( mQuestion.getQuestionUid(), "" );
        // データが存在している場合
        if( !question.equals(""))
        {
            // Questionクラスに戻す。
            Gson gson = new Gson();
            mQuestion = gson.fromJson( question, Question.class );
        }

        // 最初は必ずNULLなので、チェックを行う。
        if( mQuestion.getStar_flag() == null )
        {
            // NULLの場合には、必ずfalseを入れる。（初期値）
            mQuestion.setStar_flag( false );
        }

        // お気に入りの状態を設定
        mSwitch.setChecked(mQuestion.getStar_flag());
        /* ******************************************************************/

        // 佐野が追加した
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // ログインしていなければswitchを消す
            mSwitch.setVisibility(View.INVISIBLE);
        } else {
            // ログインしていればswitchを表示
            mSwitch.setVisibility(View.VISIBLE);
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
//        mPreference.getBoolean(mQuestion.getQuestionUid(),false);


        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(mSwitch.isChecked()) {
                    Log.v("uid", "ON");
                    //Switch保持のために追加
                    SharedPreferences.Editor editor = mPreference.edit();
                    Gson gson = new Gson();
                    mQuestion.setStar_flag( true );
                    editor.putString(mQuestion.getQuestionUid() ,gson.toJson(mQuestion));
                    editor.commit();
                    //Switch保持のために追加
                    Toast.makeText(QuestionDetailActivity.this, "お気に入りに追加しました。", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v("uid", "OFF");
                    mQuestion.setStar_flag( false );

                    /* ******************************************************************/
                    SharedPreferences.Editor editor = mPreference.edit();
                    editor.remove( mQuestion.getQuestionUid() );
                    editor.commit();
                    /* ******************************************************************/

                    //Switch保持のために追加
                    Toast.makeText(QuestionDetailActivity.this, "お気に入りから削除しました。", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //佐野が追加した

    }
}

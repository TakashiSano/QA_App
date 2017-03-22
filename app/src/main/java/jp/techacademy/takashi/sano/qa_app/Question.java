package jp.techacademy.takashi.sano.qa_app;

/**
 * Created by 307156 on 2017/03/14.
 */

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String mTitle;
    private String mBody;
    private String mName;
    private String mUid;
    private String mQuestionUid;
    private int mGenre;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;
    //佐野が追加した
    private String mStar_flag;
    //佐野が追加した

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public String getQuestionUid() {
        return mQuestionUid;
    }

    public int getGenre() {
        return mGenre;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }

    //佐野が追加した
    public String getStar_flag(){ return mStar_flag;}
    //佐野が追加した

    public void setStar_flag(String star_flag){ mStar_flag = star_flag;}

    public Question(String title, String body, String name, String uid, String questionUid, int genre, byte[] bytes, ArrayList<Answer> answers, String star_flag) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
        mStar_flag = star_flag;
    }
}
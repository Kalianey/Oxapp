package com.kalianey.oxapp.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kalianey.oxapp.R;
import com.kalianey.oxapp.models.ModelQuestion;
import com.kalianey.oxapp.models.ModelUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by kalianey on 24/08/2015.
 */
public class ProfileQuestionListAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private ModelUser user;
    private List<ModelQuestion> questions;
    private JSONArray sectionsArray;
    private LayoutInflater inflater;
    private int headerInt;
    private String headerText;

    public ProfileQuestionListAdapter(Context context, ModelUser user) {
        inflater = LayoutInflater.from(context);
        this.user = user;
        questions = user.getQuestions();
        sectionsArray = user.getSections();

    }

    @Override
    public int getCount() {
        if (questions != null) {
            return questions.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {

        if(null==questions) {
            return null;
        }
        else {
            return questions.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.profile_question_list_item, parent, false);
            holder.question = (TextView) convertView.findViewById(R.id.question);
            holder.answer = (TextView) convertView.findViewById(R.id.answer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ModelQuestion q = questions.get(position);

        String questionValue = "";

        try {
            JSONObject questionObj = new JSONObject(q.getQuestionValue());
            Log.v("JSON OBJ: ", questionObj.toString());

            int i = 0;
            for(Iterator<String> iter = questionObj.keys();iter.hasNext();) {
                String key = iter.next();
                String valueStr = questionObj.getString(key);
                questionValue += valueStr;
                i++;
                if (i < questionObj.length()) {
                    questionValue += ", ";
                }
            }

        }
        catch (Exception e) {
            e.getLocalizedMessage();
            questionValue = q.getQuestionValue();
        }

        if(q.getQuestionName().equals("Localisation")) {
            questionValue = user.getAddress();
        }


        holder.question.setText(q.getQuestionName());
        holder.answer.setText(questionValue);

        headerText = questions.get(position).getSection();

        for (int i = 0; i < sectionsArray.length(); ++i) {
            try {
                String sectionName = sectionsArray.getString(i);
                if (q.getSection() == sectionName) {
                    headerInt = i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.profile_question_list_header, parent, false);
            holder.headerText = (TextView) convertView.findViewById(R.id.header_text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        //String headerText = "" + questions.optJSONObject(position).subSequence(0, 1).charAt(0);
        holder.headerText.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        //return questions[position].subSequence(0, 1).charAt(0);

        return questions.get(position).getSection().charAt(0);
    }

    class HeaderViewHolder {
        TextView headerText;
    }

    class ViewHolder {
        TextView question;
        TextView answer;
    }


}

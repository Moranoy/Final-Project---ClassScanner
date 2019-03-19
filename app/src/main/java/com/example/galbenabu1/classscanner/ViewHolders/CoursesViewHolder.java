package com.example.galbenabu1.classscanner.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.galbenabu1.classscanner.Activities.CourseInfoActivity;
import com.example.galbenabu1.classscanner.R;

import Logic.Models.Course;

public class CoursesViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "CourseViewHolder";
    private static final String COURSE_DATA = "course_data";

    private CardView mCourseCardView;
    private TextView mCreatorName;
    private TextView mCourseName;
    private TextView mCreationDate;
    private Course mSelectedCourse;

    public CoursesViewHolder(Context context, View itemView) {
        super(itemView);

        mCourseCardView = itemView.findViewById(R.id.cvCourse);
        mCreatorName = itemView.findViewById(R.id.tvCoursePublisher);
        mCourseName = itemView.findViewById(R.id.tvCourseName);
        mCreationDate = itemView.findViewById(R.id.tvCreationDate);

        mCourseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "CardView.onClick() >> Course: " + mSelectedCourse.toString());

                Context context = view.getContext();
                Intent intent = new Intent(context, CourseInfoActivity.class);
                intent.putExtra(COURSE_DATA, mSelectedCourse);
                context.startActivity(intent);
            }
        });
    }

    public Course getSelectedCourse() {
        return mSelectedCourse;
    }

    public void setSelectedCourse(Course selectedCourse) {
        this.mSelectedCourse = selectedCourse;
    }

    public TextView getCreatorName() {
        return mCreatorName;
    }

    public void setCreatorName(TextView creatorName) {
        this.mCreatorName = creatorName;
    }

    public TextView getCourseName() {
        return mCourseName;
    }

    public void setCourseName(TextView courseName) {
        this.mCourseName = courseName;
    }

    public TextView getCreationDate() {
        return mCreationDate;
    }

    public void setCreationDate(TextView creationDate) {
        this.mCreationDate = creationDate;
    }

}

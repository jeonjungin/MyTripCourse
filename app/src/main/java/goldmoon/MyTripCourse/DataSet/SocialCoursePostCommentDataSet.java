package goldmoon.MyTripCourse.DataSet;

/**
 * 댓글
 */

public class SocialCoursePostCommentDataSet {

    private String userNick;
    private String comment;
    private String commentTime;

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}

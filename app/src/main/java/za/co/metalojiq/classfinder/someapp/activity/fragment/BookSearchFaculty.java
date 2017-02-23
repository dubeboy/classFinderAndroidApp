package za.co.metalojiq.classfinder.someapp.activity.fragment;

/**
 * Created by divine on 2/23/17.
 */
public class BookSearchFaculty extends ListBottomSheet {
    public final static String[] FACULTIES =  {"Science", "Engineering", "Law", "Architecture And Design", "Education", "Humanities", "Medicine", "Commerce", "Other"};
    public BookSearchFaculty() {
        ListBottomSheet.newInstance("Select time ", FACULTIES);
    }
}

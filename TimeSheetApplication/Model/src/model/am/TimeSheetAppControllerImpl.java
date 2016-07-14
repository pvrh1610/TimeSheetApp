package model.am;

import java.io.File;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import java.util.Map;

import model.am.common.TimeSheetAppController;

import model.vo.StEnWeekVOImpl;
import model.vo.StEnWeekVORowImpl;
import model.vo.TimeSheetAttachmentsVOImpl;
import model.vo.TimeSheetAttachmentsVORowImpl;
import model.vo.TimeSheetDaysVOImpl;
import model.vo.TimeSheetDaysVORowImpl;
import model.vo.TimeSheetRolesVOImpl;
import model.vo.TimeSheetRolesVORowImpl;
import model.vo.TimeSheetTasksVOImpl;
import model.vo.TimeSheetTasksVORowImpl;
import model.vo.TimeSheetUsersVOImpl;
import model.vo.TimeSheetUsersVORowImpl;
import model.vo.TimeSheetWeekVOImpl;
import model.vo.TimeSheetWeekVORowImpl;
import model.vo.TimesheetUsersMappingVOImpl;
import model.vo.TimesheetUsersMappingVORowImpl;
import model.vo.USerRoleTransVOImpl;
import model.vo.WeeksVVOImpl;
import model.vo.WeeksVVORowImpl;

import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.ViewLinkImpl;
import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Jun 14 12:00:47 IST 2016
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class TimeSheetAppControllerImpl extends ApplicationModuleImpl implements TimeSheetAppController {
    /**
     * This is the default constructor (do not remove).
     */
    public TimeSheetAppControllerImpl() {
    }

    /**
     * Container's getter for TimeSheetWeekVO3.
     * @return TimeSheetWeekVO3
     */
    public TimeSheetWeekVOImpl getTimeSheetWeekVO1() {
        return (TimeSheetWeekVOImpl) findViewObject("TimeSheetWeekVO1");
    }

    /**
     * Container's getter for TimeSheetDaysVO3.
     * @return TimeSheetDaysVO3
     */
    public TimeSheetDaysVOImpl getTimeSheetDaysVO1() {
        return (TimeSheetDaysVOImpl) findViewObject("TimeSheetDaysVO1");
    }

    /**
     * Container's getter for TimeSheetWeekHoursVL.
     * @return TimeSheetWeekHoursVL
     */
    public ViewLinkImpl getTimeSheetWeekHoursVL() {
        return (ViewLinkImpl) findViewLink("TimeSheetWeekHoursVL");
    }

    /**
     * Container's getter for WeeksVVO1.
     * @return WeeksVVO1
     */
    public WeeksVVOImpl getWeeksVVO1() {
        return (WeeksVVOImpl) findViewObject("WeeksVVO1");
    }

    /**
     * Container's getter for TimeSheetTasksVO1.
     * @return TimeSheetTasksVO1
     */
    public TimeSheetTasksVOImpl getTimeSheetTasksVO1() {
        return (TimeSheetTasksVOImpl) findViewObject("TimeSheetTasksVO1");
    }

    /**
     * Container's getter for TSUSERSVVO1.
     * @return TSUSERSVVO1
     */
    public ViewObjectImpl getTSUSERSVVO1() {
        return (ViewObjectImpl) findViewObject("TSUSERSVVO1");
    }

    /**
     * Container's getter for TSROLESVVO1.
     * @return TSROLESVVO1
     */
    public ViewObjectImpl getTSROLESVVO1() {
        return (ViewObjectImpl) findViewObject("TSROLESVVO1");
    }

    /**
     * Container's getter for USerRoleTransVO1.
     * @return USerRoleTransVO1
     */
    public USerRoleTransVOImpl getUSerRoleTransVO1() {
        return (USerRoleTransVOImpl) findViewObject("USerRoleTransVO1");
    }

    /**for getting week list of the given date
     * @param systemDate
     * @return
     */
    public ArrayList getWeekList(Date systemDate) {
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");
        System.out.println("getweeklist systemDate " + systemDate);
        String date = sdfr.format(systemDate);
        WeeksVVOImpl weekVO = getWeeksVVO1();
        System.out.println("getweeklist date " + date);
        weekVO.setsystemdate(date);
        weekVO.executeQuery();
        ArrayList weekList;
        weekList = new ArrayList();
        RowSetIterator rsi = weekVO.getRowSetIterator();
        WeeksVVORowImpl currentRow = null;
        while (rsi.hasNext()) {
            currentRow = (WeeksVVORowImpl) rsi.next();
            weekList.add(currentRow.getWeek());
            System.out.println("getweeklist getweek " + currentRow.getWeek());
        }
        return weekList;
    }

    /**for initializing timesheet
     * @param currentDate
     */
    public BigDecimal initTimeSheet(Date currentDate) {
        // viewTs();
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");
        String date = sdfr.format(currentDate);
        WeeksVVOImpl weekVO = getWeeksVVO1();
        weekVO.setsystemdate(date);
        weekVO.executeQuery();
        RowSetIterator itr = weekVO.getRowSetIterator();
        TimeSheetWeekVOImpl tsWeekVO = getTimeSheetWeekVO1();
        Row tsWeekRow = null;
        Date stDate = null;
        Date enDate = null;
        BigDecimal timeSheetId = null;
        while (itr.hasNext()) {
            WeeksVVORowImpl row = (WeeksVVORowImpl) itr.next();
            long ts = currentDate.getTime();
            stDate = row.getWkStarts();
            enDate = row.getWkEnds();
            if (stDate.getTime() < ts && enDate.getTime() > ts) {
                tsWeekRow = tsWeekVO.createRow();
                tsWeekRow.setAttribute("WeekStartDate", row.getWkStarts());
                tsWeekRow.setAttribute("WeekEndDate", row.getWkEnds());
                tsWeekRow.setAttribute("Status", "Open");
                tsWeekVO.insertRow(tsWeekRow);
                timeSheetId = (BigDecimal) tsWeekRow.getAttribute("TimeSheetId");
            }
        }
        itr.closeRowSetIterator();
        addTimeSheetHours(timeSheetId);
        return timeSheetId; //null;
    }

    public void createTimeSheet(Date currentDate) {
        System.out.println("inside create timesheet ...." + currentDate);
        List<String> weeksList = getWeekList(currentDate);

        for (String temp : weeksList) {
            String weekSt = temp.substring(0, 8);
            String weekEn = temp.substring(9);
            Date stDate = getDate(weekSt);
            Date enDate = getDate(weekEn);
            TimeSheetWeekVOImpl weekTsVO = getTimeSheetWeekVO1();

            boolean flag = findWeekExists(weekSt, weekEn);
            if (flag == true) {
                /*       ViewCriteria vc = weekTsVO.getViewCriteria("TsFindWeekExistsVC");

                weekTsVO.applyViewCriteria(vc);
                weekTsVO.setNamedWhereClauseParam("p_week_st_date", stDate);
                weekTsVO.setNamedWhereClauseParam("p_week_en_date", enDate);
                weekTsVO.executeQuery();*/
                break;
            } else {

                TimeSheetWeekVORowImpl weekTsRow = (TimeSheetWeekVORowImpl) weekTsVO.createRow();
                weekTsRow.setStatus("Open");
                weekTsRow.setTotalHours(0);
                weekTsRow.setWeekStartDate(new Timestamp(stDate.getTime()));
                weekTsRow.setWeekEndDate(new Timestamp(enDate.getTime()));
                weekTsVO.insertRow(weekTsRow);
                weekTsVO.setApplyViewCriteriaNames(null);
                //  weekTsVO.setCurrentRow(weekTsRow);
                // break;
            }

        }
        save();
        //weekTsRow.setWeekStartDate(value);
    }

    /** for editing timesheet hours for the given timesheetid
     * @param timeSheetId
     */

    public void editTimeSheetHours(BigDecimal timeSheetId) {

        TimeSheetWeekVOImpl weekVO = getTimeSheetWeekVO1();
        TimeSheetWeekVORowImpl weekRow = (TimeSheetWeekVORowImpl) weekVO.getCurrentRow();
        int totalHours = 0;

        TimeSheetDaysVOImpl daysVO = getTimeSheetDaysVO1();
        TimeSheetDaysVORowImpl daysRow = (TimeSheetDaysVORowImpl) daysVO.getCurrentRow();
        totalHours +=
            daysRow.getDay1() + daysRow.getDay2() + daysRow.getDay3() + daysRow.getDay4() + daysRow.getDay4() +
            daysRow.getDay5() + daysRow.getDay6() + daysRow.getDay7();
        weekRow.setTotalHours(totalHours);
    }

    /** for creating timesheet hours with default values for the given timesheet id
     *
     * @param timeSheetId
     */
    public void addTimeSheetHours(BigDecimal timeSheetId) {
        TimeSheetDaysVOImpl daysVO = getTimeSheetDaysVO1();
        TimeSheetDaysVORowImpl daysRow = (TimeSheetDaysVORowImpl) daysVO.createRow();

        daysRow.setDay1(0);
        daysRow.setDay2(0);
        daysRow.setDay3(0);
        daysRow.setDay4(0);
        daysRow.setDay5(0);
        daysRow.setDay6(0);
        daysRow.setDay7(0);
        daysVO.insertRow(daysRow);
        save();

    }

    public void submitForApproval(BigDecimal timeSheetId) {
        BigDecimal userId = getUserId();
        TimeSheetWeekVOImpl weekVO = getTimeSheetWeekVO1();
        TimeSheetWeekVORowImpl weekRow = (TimeSheetWeekVORowImpl) weekVO.getCurrentRow();
        int totalHours = 0;
        totalHours = getTimeSheetHours(timeSheetId); //weekRow.getTotalHours();
        weekRow.setTotalHours(totalHours); // + getTimeSheetHours(timeSheetId));
        weekRow.setStatus("Submitted");
        weekRow.setSubmittedBy(userId);
        weekRow.setSubmittedTs(new Timestamp(new Date().getTime()));
        submitTimeSheet(timeSheetId);
        save();
    }

    private int getTimeSheetHours(BigDecimal timeSheetId) {
        int totalHours = 0;
        TimeSheetDaysVOImpl daysVO = getTimeSheetDaysVO1();
        RowSetIterator rsi = daysVO.createRowSetIterator(null);
        TimeSheetDaysVORowImpl daysRow;
        while (rsi.hasNext()) {
            daysRow = (TimeSheetDaysVORowImpl) rsi.next();
            totalHours +=
                daysRow.getDay1() + daysRow.getDay2() + daysRow.getDay3() + daysRow.getDay4() + daysRow.getDay4() +
                daysRow.getDay5() + daysRow.getDay6() + daysRow.getDay7();
        }
        rsi.closeRowSetIterator();
        return totalHours;
    }

    public Boolean deleteTimeSheetHours(BigDecimal timeSheetId) {
        TimeSheetDaysVOImpl daysVO = getTimeSheetDaysVO1();
        TimeSheetWeekVOImpl weekVO = getTimeSheetWeekVO1();
        TimeSheetWeekVORowImpl weekRow = (TimeSheetWeekVORowImpl) weekVO.getCurrentRow();
        TimeSheetDaysVORowImpl daysRow = (TimeSheetDaysVORowImpl) daysVO.getCurrentRow();
        daysRow.remove();
        long count = daysVO.getEstimatedRowCount();
        weekRow.setStatus("Open");
        if (count == 0) {
            weekRow.setTotalHours(0);
            save();
            return Boolean.FALSE;
        } else {
            int totalHours = getTimeSheetHours(timeSheetId);
            weekRow.setTotalHours(totalHours);
            save();
            return Boolean.TRUE;
        }
    }

    public void unSubmitTimeSheet(BigDecimal timeSheetId) {
        TimeSheetWeekVOImpl weekVO = getTimeSheetWeekVO1();
        TimeSheetWeekVORowImpl weekRow = (TimeSheetWeekVORowImpl) weekVO.getCurrentRow();
        if (weekRow.getStatus().equalsIgnoreCase("Submitted")) {
            weekRow.remove();
        }
        save();
    }

    /**
     * Container's getter for TimesheetUsersMappingVO1.
     * @return TimesheetUsersMappingVO1
     */
    public TimesheetUsersMappingVOImpl getTimesheetUsersMappingVO1() {
        return (TimesheetUsersMappingVOImpl) findViewObject("TimesheetUsersMappingVO1");
    }

    private void submitTimeSheet(BigDecimal timeSheetId) {
        TimesheetUsersMappingVOImpl mappingVO = getTimesheetUsersMappingVO1();
        TimesheetUsersMappingVORowImpl mappingRow = (TimesheetUsersMappingVORowImpl) mappingVO.createRow();
        mappingRow.setTimeSheetId(timeSheetId);
        mappingRow.setSubmittedBy(getUserId());
        //mappingRow.setSubmittedTo(value);
        mappingVO.insertRow(mappingRow);
        save();
    }

    public void approveTimeSheet(BigDecimal timeSheetID, String comments) {
        System.out.println("inside approvetimesheet");
        TimeSheetWeekVOImpl timeSheetVO = getTimeSheetWeekVO1();
        TimeSheetWeekVORowImpl timeSheetRow = (TimeSheetWeekVORowImpl) timeSheetVO.getCurrentRow();
        timeSheetRow.setStatus("Approved");
        timeSheetRow.setApproverComments(comments);
        System.out.println("added status");
        timeSheetRow.setApprovedBy(getUserId());
        timeSheetRow.setApprovedTs(new Timestamp(new Date().getTime()));
        //mappingRow.setSubmittedBy(value);
        //mappingRow.setSubmittedTo(value);
        //mappingRow.setApprovedBy(value);
        getDBTransaction().commit();
    }

    public void rejectTimeSheet(BigDecimal timeSheetID, String comments) {
        System.out.println("inside rejectTimeSheet");
        TimeSheetWeekVOImpl timeSheetVO = getTimeSheetWeekVO1();
        TimeSheetWeekVORowImpl timeSheetRow = (TimeSheetWeekVORowImpl) timeSheetVO.getCurrentRow();
        timeSheetRow.setStatus("Rejected");
        timeSheetRow.setApproverComments(comments);
        System.out.println("added status");
        //mappingRow.setSubmittedBy(value);
        //mappingRow.setSubmittedTo(value);
        //mappingRow.setApprovedBy(value);
        save();
    }

    public void viewTs() {
        TimeSheetWeekVOImpl tsWeekVO = getTimeSheetWeekVO1();
        long rowCount = tsWeekVO.getEstimatedRowCount();
        Date currentDate = new Date();
        if (rowCount == 0) {
            //  initTimeSheet(currentDate);
            System.out.println("no rows");
        } else {

            SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");
            String date = sdfr.format(currentDate);
            String whereClause =
                "SUBMITTED_BY=" +
                getUserId(); //+" AND STATUS = 'Open' or STATUS='Submitted'";// or STATUS = 'Pending'"// or (to_date('13-06-16') between WEEK_START_DATE and WEEK_END_DATE)";
            tsWeekVO.setWhereClause(whereClause);
            tsWeekVO.executeQuery();
        }
    }

    /**
     * Container's getter for TimeSheetRolesVO1.
     * @return TimeSheetRolesVO1
     */
    public TimeSheetRolesVOImpl getTimeSheetRolesVO1() {
        return (TimeSheetRolesVOImpl) findViewObject("TimeSheetRolesVO1");
    }

    /**
     * Container's getter for TimeSheetUsersVO1.
     * @return TimeSheetUsersVO1
     */
    public TimeSheetUsersVOImpl getTimeSheetUsersVO1() {
        return (TimeSheetUsersVOImpl) findViewObject("TimeSheetUsersVO1");
    }

    /**
     * Container's getter for TimeSheetUsersVO2.
     * @return TimeSheetUsersVO2
     */
    public TimeSheetUsersVOImpl getTimeSheetUsersVO2() {
        return (TimeSheetUsersVOImpl) findViewObject("TimeSheetUsersVO2");
    }

    /**
     * Container's getter for TimeSheetUsersRoleIdFkLink1.
     * @return TimeSheetUsersRoleIdFkLink1
     */
    public ViewLinkImpl getTimeSheetUsersRoleIdFkLink1() {
        return (ViewLinkImpl) findViewLink("TimeSheetUsersRoleIdFkLink1");
    }

    public void saveUsers(BigDecimal userId) {
        TimeSheetUsersVOImpl usersVO = getTimeSheetUsersVO1();
        TimeSheetUsersVORowImpl usersRow = (TimeSheetUsersVORowImpl) usersVO.getRow(new Key(new Object[] { userId }));
        usersVO.setCurrentRow(usersRow);
    }

    public void createUsers() {
        TimeSheetUsersVOImpl usersVO = getTimeSheetUsersVO1();
        TimeSheetUsersVORowImpl row = (TimeSheetUsersVORowImpl) usersVO.createRow();
        usersVO.insertRow(row);
        save();
    }

    public void createRoles() {
        TimeSheetRolesVOImpl rolesVO = getTimeSheetRolesVO1();
        TimeSheetRolesVORowImpl row = (TimeSheetRolesVORowImpl) rolesVO.createRow();
        rolesVO.insertRow(row);
        save();
    }

    public void saveRoles(BigDecimal roleId) {
        TimeSheetRolesVOImpl rolesVO = getTimeSheetRolesVO1();
        TimeSheetRolesVORowImpl rolesRow = (TimeSheetRolesVORowImpl) rolesVO.getRow(new Key(new Object[] { roleId }));
        rolesVO.setCurrentRow(rolesRow);
        // getDBTransaction().commit();
    }

    public Boolean deleteTimeSheetRoles() {
        TimeSheetRolesVOImpl rolesVO = getTimeSheetRolesVO1();
        TimeSheetRolesVORowImpl rolesRow = (TimeSheetRolesVORowImpl) rolesVO.getCurrentRow();
        rolesRow.remove();
        long count = rolesVO.getEstimatedRowCount();
        if (count == 0) {
            //     getDBTransaction().commit();
            save();
            return Boolean.FALSE;
        } else {
            //getDBTransaction().commit();
            save();
            return Boolean.TRUE;
        }
    }

    public void save() {
        this.getDBTransaction().commit();
    }

    public void cancel() {
        this.getDBTransaction().rollback();
    }

    public void createTasks() {
        TimeSheetTasksVOImpl tasksVO = getTimeSheetTasksVO1();
        TimeSheetTasksVORowImpl tasksRow = (TimeSheetTasksVORowImpl) tasksVO.createRow();
        tasksVO.insertRow(tasksRow);
        save();
    }

    public void saveTasks(BigDecimal taskId) {
        TimeSheetTasksVOImpl tasksVO = getTimeSheetTasksVO1();
        TimeSheetTasksVORowImpl tasksRow = (TimeSheetTasksVORowImpl) tasksVO.getRow(new Key(new Object[] { taskId }));
        tasksVO.setCurrentRow(tasksRow);
    }

    public Boolean deleteTimeSheetTasks() {
        TimeSheetTasksVOImpl tasksVO = getTimeSheetTasksVO1();
        TimeSheetTasksVORowImpl tasksRow = (TimeSheetTasksVORowImpl) tasksVO.getCurrentRow();
        tasksRow.remove();
        long count = tasksVO.getEstimatedRowCount();
        if (count == 0) {
            //     getDBTransaction().commit();
            return Boolean.FALSE;
        } else {
            //getDBTransaction().commit();
            return Boolean.TRUE;
        }
    }

    public Boolean deleteTimeSheetUsers() {
        TimeSheetUsersVOImpl usersVO = getTimeSheetUsersVO1();
        TimeSheetUsersVORowImpl usersRow = (TimeSheetUsersVORowImpl) usersVO.getCurrentRow();
        usersRow.remove();
        long count = usersVO.getEstimatedRowCount();
        if (count == 0) {
            //     getDBTransaction().commit();
            return Boolean.FALSE;
        } else {
            //getDBTransaction().commit();
            return Boolean.TRUE;
        }
    }

    private boolean findWeekExists(String weekSt, String weekEn) {
        //check from timesheetweek
        TimeSheetWeekVOImpl weekVO = getTimeSheetWeekVO1();

        ViewCriteria vc = weekVO.getViewCriteria("TsFindWeekExistsVC");

        weekVO.applyViewCriteria(vc);
        weekVO.setNamedWhereClauseParam("p_week_st_date", getDate(weekSt));
        weekVO.setNamedWhereClauseParam("p_week_en_date", getDate(weekEn));
        weekVO.executeQuery();

        long count = weekVO.getEstimatedRowCount();
        //  weekVO.setApplyViewCriteriaNames(null);
        System.out.println("count..." + count);
        if (count > 0) {
            return true;
        }
        return false;
    }

    private Date getDate(String dateStr) {
        System.out.println("inside getdate" + dateStr);
        DateFormat format = new SimpleDateFormat("dd-MM-yy");
        Date d = null;
        try {
            d = format.parse(dateStr);
            System.out.println("date..." + d);
        } catch (ParseException e) {
        }
        return d;
    }

    /**
     * Container's getter for TimeSheetAttachmentsVO1.
     * @return TimeSheetAttachmentsVO1
     */
    public TimeSheetAttachmentsVOImpl getTimeSheetAttachmentsVO1() {
        return (TimeSheetAttachmentsVOImpl) findViewObject("TimeSheetAttachmentsVO1");
    }

    public void uploadFile(String fileName, String contentType, BlobDomain file) {
        TimeSheetAttachmentsVOImpl vo = getTimeSheetAttachmentsVO1();
        TimeSheetAttachmentsVORowImpl row = (TimeSheetAttachmentsVORowImpl) vo.createRow();
        TimeSheetDaysVOImpl tsVO = getTimeSheetDaysVO1();
        TimeSheetDaysVORowImpl tsRow = (TimeSheetDaysVORowImpl) tsVO.getCurrentRow();
        BigDecimal weekId = tsRow.getWeekId();

        row.setTimesheetId(weekId);
        System.out.println("service current id..." + weekId);
        row.setFileName(fileName);
        row.setFileType(contentType);
        row.setAttachedFile(file);
        row.setCreatedBy(getUserId());
        row.setCreatedDate(new Timestamp(new Date().getTime()));
        vo.insertRow(row);
        save();
        vo.setWhereClause("CREATED_BY=" + getUserId());
        vo.executeQuery();
    }

    public void initSubmittedTS(String status) {
        TimeSheetWeekVOImpl vo = getTimeSheetWeekVO1();
        System.out.println("In servive initsubmit" + status);
        if (status.equalsIgnoreCase("Submitted")) {
            //  vo.setWhereClause("STATUS ='Approved' or  or STATUS='Rejected'");
            //  System.out.println("In Submitted");
            //  vo.executeQuery();
            vo.setWhereClause("STATUS='Submitted' AND SUBMITTED_BY IN (select user_id from time_sheet_users where MANAGER_ID=" +
                              getUserId() + ")");
            vo.executeQuery();
        } //if (status.equalsIgnoreCase("All")) {
        else {
            vo.setWhereClause(null);
            vo.executeQuery();
            vo.setWhereClause("SUBMITTED_BY=" + getUserId());
            // vo.setSortBy("WEEK_START_DATE desc");
            vo.executeQuery();
            System.out.println("In all");
        }

        System.out.println("executed query");
    }

    public void updatePassword(String userName, String password) {
        TimeSheetUsersVOImpl vo = getTimeSheetUsersVO1();
        vo.setWhereClause("USER_NAME='" + userName + "'");
        vo.executeQuery();
        long count = vo.getEstimatedRowCount();
        if (count > 0) {
            TimeSheetUsersVORowImpl row = (TimeSheetUsersVORowImpl) vo.first();
            row.setPassword(password);
        }
        save();
    }

    public void saveRoleChange(BigDecimal userId, BigDecimal roleId) {
        TimeSheetUsersVOImpl vo = getTimeSheetUsersVO1();
        System.out.println("serv user ID...." + userId);
        vo.setWhereClause("USER_ID =" + userId);
        vo.executeQuery();
        long count = vo.getEstimatedRowCount();
        if (count > 0) {
            TimeSheetUsersVORowImpl row = (TimeSheetUsersVORowImpl) vo.first();
            row.setRoleId(roleId);
            System.out.println("serv Role ID...." + roleId);
        }
        save();
        vo.setWhereClause(null);
        vo.executeQuery();
    }

    public void saveUserChange() {
        save();
    }

    private BigDecimal getUserId() {
        Map sessionScope = ADFContext.getCurrent().getSessionScope();
        String userName = (String) sessionScope.get("userName");
        TimeSheetUsersVOImpl vo = getTimeSheetUsersVO1();
        vo.setWhereClause("USER_NAME='" + userName + "'");
        vo.executeQuery();
        BigDecimal userId = null;
        if (vo.getEstimatedRowCount() > 0) {
            TimeSheetUsersVORowImpl row = (TimeSheetUsersVORowImpl) vo.first();
            userId = row.getUserId();

        }
        vo.setWhereClause(null);
        return userId;
    }

    public void saveTimeSheet(BigDecimal timeSheetId) {
        BigDecimal userId = getUserId();
        TimeSheetWeekVOImpl weekVO = getTimeSheetWeekVO1();
        TimeSheetWeekVORowImpl weekRow = (TimeSheetWeekVORowImpl) weekVO.getCurrentRow();
        int totalHours = 0;
        totalHours = getTimeSheetHours(timeSheetId); //weekRow.getTotalHours();
        weekRow.setTotalHours(totalHours); // + getTimeSheetHours(timeSheetId));
        weekRow.setStatus("Open");
        weekRow.setSubmittedBy(userId);
        weekRow.setSubmittedTs(new Timestamp(new Date().getTime()));
        //   submitTimeSheet(timeSheetId);
        save();
    }

    public void createNewTimeSheet(Date currentDate) {
        StEnWeekVOImpl vo = getStEnWeekVO1();
        vo.setNamedWhereClauseParam("systemdate", new java.sql.Date(currentDate.getTime()));
        vo.executeQuery();
        if (vo.getEstimatedRowCount() > 0) {
            System.out.println("in side if count...");
            StEnWeekVORowImpl row = (StEnWeekVORowImpl) vo.first();
            TimeSheetWeekVOImpl tsWeekVo = getTimeSheetWeekVO1();
            Date wkStDate = row.getWeekStartDate();
            Date wkEnDate = row.getWeekEndDate();
            SimpleDateFormat form = new SimpleDateFormat("dd/MMM/yyyy");
            String temp = form.format(wkStDate);
            tsWeekVo.setWhereClause("WEEK_START_DATE = '" + temp + "' AND SUBMITTED_BY=" + getUserId());
            tsWeekVo.executeQuery();

            if (tsWeekVo.getEstimatedRowCount() == 0) {
                System.out.println("inside tsweek if " + wkStDate + "end date..." + wkEnDate);
                TimeSheetWeekVORowImpl weekTsRow = (TimeSheetWeekVORowImpl) tsWeekVo.createRow();
                weekTsRow.setStatus("Open");
                weekTsRow.setTotalHours(0);
                weekTsRow.setWeekStartDate(new Timestamp(wkStDate.getTime()));
                weekTsRow.setWeekEndDate(new Timestamp(wkEnDate.getTime()));
                weekTsRow.setSubmittedBy(getUserId());
                weekTsRow.setSubmittedTs(new Timestamp(new Date().getTime()));
                tsWeekVo.insertRow(weekTsRow);
                System.out.println("inserted....row");
                // tsWeekVo.setApplyViewCriteriaNames(null);
                // tsWeekVo.setWhereClause(null);
                //  tsWeekVo.executeQuery();
                System.out.println("view criteia to null...");
            }
        }


    }

    /**
     * Container's getter for StEnWeekVO1.
     * @return StEnWeekVO1
     */
    public StEnWeekVOImpl getStEnWeekVO1() {
        return (StEnWeekVOImpl) findViewObject("StEnWeekVO1");
    }

    public void ViewAllTs() {
        TimeSheetWeekVOImpl tsWeekVo = getTimeSheetWeekVO1();
        tsWeekVo.setWhereClause("SUBMITTED_BY=" + getUserId());
        tsWeekVo.executeQuery();
    }

    public void getAttachments() {
        TimeSheetAttachmentsVOImpl vo = getTimeSheetAttachmentsVO1();
        vo.setWhereClause("CREATED_BY=" + getUserId());
        vo.executeQuery();
    }

    public void getUsersList() {
        TimeSheetUsersVOImpl vo = getTimeSheetUsersVO1();
        vo.setWhereClause(null);
        vo.executeQuery();

    }
}

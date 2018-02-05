/*
 * ETLConstants.java
 *
 * Created on 2008��2��3��, ����9:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.aspire.etl.plugins;

/**
 * 
 * @author x_lixin_a
 */
public class ETLConstants {

	public static final String FLAG_ZERO = "0";

	public static final String FLAG_ONE = "1";

	public static final String FLAG_TWO = "2";

	public static final String FLAG_THREE = "3";

	public static final int FLAG_INT_ZERO = 0;

	public static final int FLAG_INT_ONE = 1;

	public static final int FLAG_INT_TWO = 2;

	public static final int FLAG_INT_THREE = 3;

	// ǰ������SQL,û����Ϊ��
	//public static final String CON_SELECT = "Con_Select";
        
    //���ʴ���ȴ�ʱ��
    public static final String WAIT_TIME = "WAIT_TIME";

	// �����ļ�·��
	public static final String IN_FILE_PATH = "InFile_Path";

	// �����ļ�ģʽ(ģʽ����yyyy��ʾ��,mm��ʾ��,dd��ʾ��,hh��ʾСʱ,*��ʾ��������ַ�)(��:ppsvisit_yyyymmdd_*.log)
	public static final String IN_FILE_PATTERN = "InFile_Pattern";

	public static final String IN_FILE_FIELD_NAME = "InFile_FieldName";
	public static final String IN_FILE_METHOD = "InFile_Method";
	public static final String IN_FILE_DELIMIT = "InFile_Delimit";
	public static final String IN_FILE_FIELD_LENGTH = "InFile_FieldLength";
	public static final String OUT_FILE_PATH = "OutFile_Path";
	public static final String OUT_FILE_PATTERN = "OutFile_Pattern";
	public static final String OUT_FILE_FIELD_NAME = "OutFile_FieldName";
	public static final String OUTFILE_DELIMIT = "OutFile_Delimit";
	public static final String INFILE_COLON = "InFile_Colon";
	public static final String INFILE_CHECK_FIELD_NAME = "InFile_CheckFieldName";
	public static final String INFILE_CHECK_TYPE = "InFile_CheckType";
	public static final String INFILE_HEAD_NUM = "InFile_HeadNum";
	public static final String INFILE_TAIL_NUM = "InFile_TailNum";
	public static final String INFILE_LINE_HEAD = "InFile_LineHead";
	public static final String INFILE_LINE_TAIL = "InFile_LineTail";
	public static final String ISOUT_FILETIME = "IsOut_FileTime";

	public static final String CALL_AUDITOR = "Call_Auditor";
	public static final String CHECK_FILE_DELIMIT = "CheckFile_Delimit";
	public static final String CHECK_FILE_NUM = "CheckFile_Num";
	public static final String CHECK_FILE_ENDFLAG = "CheckFile_EndFlag";
	public static final String CHECK_FILE_NAMEPOS = "CheckFile_NamePos";
	public static final String CHECK_FILE_LINEPOS = "CheckFile_LinePos";
	public static final String CHECK_FILE_SIZEPOS = "CheckFile_SizePos";
	public static final String CHECK_FILE_TIMEPOS = "CheckFile_TimePos";
	public static final String CHECK_FILE_LINEADJUST = "CheckFile_LineAdjust";
	public static final String CHECK_FILE_PATH = "CheckFile_Path";
	public static final String CHECK_FILE_PATTERN = "CheckFile_Pattern";
	public static final String DATA_FILE_PATH = "DataFile_Path";
	public static final String DATA_FILE_PATTERN = "DataFile_Pattern";
        public static final String DELAY_AUDITOR_HH = "DELAYAUDITORHH";

	public static final String SQL_DELETE = "Sql_Delete";
	public static final String SQL_SELECT = "Sql_Select";
	public static final String SQL_INSERT = "Sql_Insert";
	public static final String TRANS_FIELD_SQL = "Trans_Field_Sql";
	public static final String LOG_TO_FILE = "LogToFile";
	public static final String LOG_TO_OAM = "LogToOAM";
	public static final String LOG_TO_DB = "LogToDB";
	public static final String DB_CONN_CFG = "dbconn_cfg";
	public static final String LOG_PATH = "LogPath";
	public static final String INTERVAL = "Interval";
	public static final String RUN_FLAG = "RunFlag";
	public static final String EXIT_CTRL_FILE = "ExitCtrlFile";

	public static final String SHELL_NAME = "Shell_Name";
	public static final String SHELL_WORKDIR = "Shell_WorkDir";
        
    public static final String FLOW_NAME = "FLOW_NAME";
	public static final String CHECK_INTERVAL_TIME = "CHECK_INTERVAL_TIME";

	public static final String SOURCE_DB_ALIAS = "Source_DB_Alias";
	public static final String TARGET_DB_ALIAS = "Target_DB_Alias";
	public static final String SYN_SQL = "Syn_Sql";

	// �洢��������
	public static final String ACTION = "Action";
	// ��̬������
	public static final String DYNAMIC1 = "Dynamic1";
	// ��̬����2
	public static final String DYNAMIC2 = "Dynamic2";
	// Դ��
	public static final String SOURCE_TABLE = "SOURCE_TABLE";
	// �����߼�
	public static final String PROCESS_LOGIC = "PROCESS_LOGIC";
	// Ŀ���
	public static final String TARGET_TABLE = "TARGET_TABLE";
        
    //ͳ��==============        
    public static final String infile_folder="infile_folder";
    public static final String infile_pattern="infile_pattern";
    public static final String infile_all_field_name_list="infile_all_field_name_list";
    public static final String infile_valid_field_name_list="infile_valid_field_name_list";
    public static final String infile_valid_field_max_value_number="infile_valid_field_max_value_number";
    public static final String infile_phone_number_field_name="infile_phone_number_field_name";
    public static final String infile_phone_number_max_len="infile_phone_number_max_len";
    public static final String infile_inter_field_delimit="infile_inter_field_delimit";
    
    public static final String infile_need_all_operator_fields_list="infile_need_all_operator_fields_list";
    public static final String outfile_want_bill_file="outfile_want_bill_file";
    public static final String outfile_bill_file_folder="outfile_bill_file_folder";
    public static final String outfile_bill_file_pattern="outfile_bill_file_pattern";
    public static final String outfile_want_stat_file="outfile_want_stat_file";
    public static final String outfile_stat_file_folder="outfile_stat_file_folder";
    public static final String outfile_stat_file_pattern="outfile_stat_file_pattern";
    public static final String outfile_inter_field_delimit="outfile_inter_field_delimit";
    public static final String outfile_field_equal_all_desc="outfile_field_equal_all_desc";
    
        
    //���ݿ����
    public static final String DB_ALIAS = "DB_Alias";
    //����
    public static final String SP_NAME = "Sp_Name"; 
    //����ֵ
    public static final String PARAM_VALUE = "Param_Value"; 
    //������
    public static final String PARAM_KEY = "Param_Key"; 
    
    public static final String IS_PROC = "Is_Proc"; 

	public static final String TIME_ERROR_MESSAGE_START_TIME = "ͳ��ʱ���������! \n�������ʱ�䲻��һ����ȷ��ʱ��������ʱ���ʽ����yyyy-MM-dd HH:mm:ss��ʽ��";

	public static final String TIME_ERROR_MESSAGE_SCENE_START_TIME = "����ʱ���������!\n�������ʱ�䲻��һ����ȷ��ʱ��������ʱ���ʽ����yyyy-MM-dd HH:mm:ss��ʽ��";

	public static final String TIME_ERROR_MESSAGE_REDO_START_TIME = "������ʼʱ���������!\n�������ʱ�䲻��һ����ȷ��ʱ��������ʱ���ʽ����yyyy-MM-dd HH:mm:ss��ʽ��";

	public static final String TIME_ERROR_MESSAGE_REDO_END_TIME = "��������ʱ���������!\n�������ʱ�䲻��һ����ȷ��ʱ��������ʱ���ʽ����yyyy-MM-dd HH:mm:ss��ʽ��";

	public static final String TIME_ERROR_TITLE_MESSAGE = "�������!";

	public static final String ERROR_MESSAGE_SCENE_START_TIME = "ͳ��ʱ��Ӧ��С�ڳ���ʱ�䣡";

	public static final String ERROR_MESSAGE_REDO_START_TIME = "ͳ��ʱ��Ӧ�õ���������ʼʱ�䣡";

	public static final String ERROR_MESSAGE_REDO_END_TIME = "ͳ��ʱ��Ӧ��С�ڻ������������ʱ�䣡";

	public static final String TASK_FLOW_TEXT_FIELD_ERROR_MESSAGE = "���������ֲ��������������ַ�!!!";

	public static final String TIME_00 = "00";

	public static final String TIME_01 = "01";
        
	//�ļ��ϴ�����
    public static final String PUT_URL = "Put_Url";
    public static final String PUT_PORT = "Put_Port";
    public static final String PUT_USERNAME = "Put_UserName";
    public static final String PUT_PASSWORD = "Put_Password";
    public static final String PUT_REMOTE_PATH = "Put_Remote_Path";
    public static final String PUT_LOCAL_PATH = "Put_Local_Path";
    public static final String PUT_MODE = "Put_Mode";
    public static final String PUT_WILDCARD = "Put_Wildcard";
    
    //�ļ���������
    public static final String GET_URL = "Get_Url";
    public static final String GET_PORT = "Get_Port";
    public static final String GET_USERNAME = "Get_UserName";
    public static final String GET_PASSWORD = "Get_Password";
    public static final String GET_REMOTE_PATH = "Get_Remote_Path";
    public static final String GET_LOCAL_PATH = "Get_Local_Path";
    public static final String GET_MODE = "Get_Mode";
    public static final String GET_WILDCARD = "Get_Wildcard";
    
    //�ʼ���������
    
	/**
	 * Creates a new instance of ETLConstants
	 */
	public ETLConstants() {
	}

}

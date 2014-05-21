//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@Enumeration 
public class FunctionEnumeration {

	public java.lang.String string_equal="STRING_EQUAL";

	public java.lang.String boolean_equal="BOOLEAN_EQUAL";

	public java.lang.String integer_equal="INTEGER_EQUAL";

	public java.lang.String double_equal="DOUBLE_EQUAL";

	public java.lang.String date_equal="DATE_EQUAL";

	public java.lang.String time_equal="TIME_EQUAL";

	public java.lang.String dateTime_equal="DATETIME_EQUAL";

	public java.lang.String dayTimeDuration_equal="DAYTIMEDURATION_EQUAL";

	public java.lang.String yearMonthDuration_equal="YEARMONTHDURATION_EQUAL";

	public java.lang.String anyURI_equal="ANYURI_EQUAL";

	public java.lang.String x500Name_equal="X500NAME_EQUAL";

	public java.lang.String rfc822Name_equal="RFC822NAME_EQUAL";

	public java.lang.String hexBinary_equal="HEXBINARY_EQUAL";

	public java.lang.String base64Binary_equal="BASE64BINARY_EQUAL";

	public java.lang.String integer_add="INTEGER_ADD";

	public java.lang.String double_add="DOUBLE_ADD";

	public java.lang.String integer_subtract="INTEGER_SUBTRACT";

	public java.lang.String double_subtract="DOUBLE_SUBTRACT";

	public java.lang.String integer_multiply="INTEGER_MULTIPLY";

	public java.lang.String double_multiply="DOUBLE_MULTIPLY";

	public java.lang.String double_divide="DOUBLE_DIVIDE";

	public java.lang.String integer_divide="INTEGER_DIVIDE";

	public java.lang.String integer_mod="INTEGER_MOD";

	public java.lang.String integer_abs="INTEGER_ABS";

	public java.lang.String double_abs="DOUBLE_ABS";

	public java.lang.String round="ROUND";

	public java.lang.String floor="FLOOR";

	public java.lang.String string_normalize_space="STRING_NORMALIZE_SPACE";

	public java.lang.String string_normalize_to_lower_case="STRING_NORMALIZE_TO_LOWER_CASE";

	public java.lang.String double_to_integer="DOUBLE_TO_INTEGER";

	public java.lang.String integer_to_double="INTEGER_TO_DOUBLE";

	public java.lang.String or="OR";

	public java.lang.String and="AND";

	public java.lang.String n_of="N_OF";

	public java.lang.String not="NOT";

	public java.lang.String integer_greater_than="INTEGER_GREATER_THAN";

	public java.lang.String integer_greater_than_or_equal="INTEGER_GREATER_THAN_OR_EQUAL";

	public java.lang.String integer_less_than="INTEGER_LESS_THAN";

	public java.lang.String integer_less_than_or_equal="INTEGER_LESS_THAN_OR_EQUAL";

	public java.lang.String double_greater_than="DOUBLE_GREATER_THAN";

	public java.lang.String double_greater_than_or_equal="DOUBLE_GREATER_THAN_OR_EQUAL";

	public java.lang.String double_less_than="DOUBLE_LESS_THAN";

	public java.lang.String double_less_than_or_equal="DOUBLE_LESS_THAN_OR_EQUAL";

	public java.lang.String dateTime_add_dayTimeDuration="DATETIME_ADD_DAYTIMEDURATION";

	public java.lang.String dateTime_add_yearMonthDuration="DATETIME_ADD_YEARMONTHDURATION";

	public java.lang.String dateTime_subtract_dayTimeDuration="DATETIME_SUBTRACT_DAYTIMEDURATION";

	public java.lang.String dateTime_subtract_yearMonthDuration="DATETIME_SUBTRACT_YEARMONTHDURATION";

	public java.lang.String date_add_yearMonthDuration="DATE_ADD_YEARMONTHDURATION";

	public java.lang.String date_subtract_yearMonthDuration="DATE_SUBTRACT_YEARMONTHDURATION";

	public java.lang.String string_greater_than="STRING_GREATER_THAN";

	public java.lang.String string_greater_than_or_equal="STRING_GREATER_THAN_OR_EQUAL";

	public java.lang.String string_less_than="STRING_LESS_THAN";

	public java.lang.String string_less_than_or_equal="STRING_LESS_THAN_OR_EQUAL";

	public java.lang.String time_greater_than="TIME_GREATER_THAN";

	public java.lang.String time_greater_than_or_equal="TIME_GREATER_THAN_OR_EQUAL";

	public java.lang.String time_less_than="TIME_LESS_THAN";

	public java.lang.String time_less_than_or_equal="TIME_LESS_THAN_OR_EQUAL";

	public java.lang.String time_in_range="TIME_IN_RANGE";

	public java.lang.String dateTime_greater_than="DATETIME_GREATER_THAN";

	public java.lang.String dateTime_greater_than_or_equal="DATETIME_GREATER_THAN_OR_EQUAL";

	public java.lang.String dateTime_less_than="DATETIME_LESS_THAN";

	public java.lang.String dateTime_less_than_or_equal="DATETIME_LESS_THAN_OR_EQUAL";

	public java.lang.String date_greater_than="DATE_GREATER_THAN";

	public java.lang.String date_greater_than_or_equal="DATE_GREATER_THAN_OR_EQUAL";

	public java.lang.String date_less_than="DATE_LESS_THAN";

	public java.lang.String date_less_than_or_equal="DATE_LESS_THAN_OR_EQUAL";

	public java.lang.String string_concatenate="STRING_CONCATENATE";

	public java.lang.String uri_string_concatenate="URI_STRING_CONCATENATE";

	public java.lang.String type_one_and_only="TYPE_ONE_AND_ONLY";

	public java.lang.String type_bag_size="TYPE_BAG_SIZE";

	public java.lang.String type_is_in="TYPE_IS_IN";

	public java.lang.String type_bag="TYPE_BAG";

	public java.lang.String type_intersection="TYPE_INTERSECTION";

	public java.lang.String type_at_least_one_member_of="TYPE_AT_LEAST_ONE_MEMBER_OF";

	public java.lang.String type_union="TYPE_UNION";

	public java.lang.String type_subset="TYPE_SUBSET";

	public java.lang.String type_set_equals="TYPE_SET_EQUALS";

	public java.lang.String any_of="ANY_OF";

	public java.lang.String all_of="ALL_OF";

	public java.lang.String any_of_any="ANY_OF_ANY";

	public java.lang.String all_of_any="ALL_OF_ANY";

	public java.lang.String any_of_all="ANY_OF_ALL";

	public java.lang.String all_of_all="ALL_OF_ALL";

	public java.lang.String map="MAP";

	public java.lang.String string_regexp_match="STRING_REGEXP_MATCH";

	public java.lang.String anyURI_regexp_match="ANYURI_REGEXP_MATCH";

	public java.lang.String ipAddress_regexp_match="IPADDRESS_REGEXP_MATCH";

	public java.lang.String dnsName_regexp_match="DNSNAME_REGEXP_MATCH";

	public java.lang.String rfc822Name_regexp_match="RFC822NAME_REGEXP_MATCH";

	public java.lang.String x500Name_regexp_match="X500NAME_REGEXP_MATCH";

	public java.lang.String x500Name_match="X500NAME_MATCH";

	public java.lang.String rfc822Name_match="RFC822NAME_MATCH";

	public java.lang.String xpath_node_count="XPATH_NODE_COUNT";

	public java.lang.String xpath_node_equal="XPATH_NODE_EQUAL";

	public java.lang.String xpath_node_match="XPATH_NODE_MATCH";

}

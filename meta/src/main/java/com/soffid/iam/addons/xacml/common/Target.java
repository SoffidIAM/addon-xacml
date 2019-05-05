//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.addons.xacml.common;
import com.soffid.mda.annotation.*;

@ValueObject 
public class Target {

	@Nullable
	public java.lang.Long id;

	@Nullable
	public java.lang.String targetComment;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.SubjectMatch> subjectMatch;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.ActionMatch> actionMatch;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.ResourceMatch> resourceMatch;

	@Nullable
	public java.util.Collection<com.soffid.iam.addons.xacml.common.EnvironmentMatch> environmentMatch;

}

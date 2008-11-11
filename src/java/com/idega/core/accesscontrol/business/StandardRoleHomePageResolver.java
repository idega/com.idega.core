package com.idega.core.accesscontrol.business;

import com.idega.idegaweb.IWResourceBundle;

public enum StandardRoleHomePageResolver {
	
	ADMIN {
		
		@Override
		public String getLocalizedName(IWResourceBundle iwrb) {
			return iwrb.getLocalizedString(getRoleKeyForLocalization(StandardRoles.ROLE_KEY_ADMIN), StandardRoles.ROLE_KEY_ADMIN);
		}

		@Override
		public String getUri() {
			return "/workspace";
		}
		
	},
	BUILDER {

		@Override
		public String getLocalizedName(IWResourceBundle iwrb) {
			return iwrb.getLocalizedString(getRoleKeyForLocalization(StandardRoles.ROLE_KEY_BUILDER), StandardRoles.ROLE_KEY_BUILDER);
		}

		@Override
		public String getUri() {
			return new StringBuilder(ADMIN.getUri()).append("/content/pages").toString();
		}
		
	},
	DEVELOPER {

		@Override
		public String getLocalizedName(IWResourceBundle iwrb) {
			return iwrb.getLocalizedString(getRoleKeyForLocalization(StandardRoles.ROLE_KEY_BUILDER), StandardRoles.ROLE_KEY_BUILDER);
		}

		@Override
		public String getUri() {
			return new StringBuilder(ADMIN.getUri()).append("/developer").toString();
		}
		
	},
	USER_APPLICATION {

		@Override
		public String getLocalizedName(IWResourceBundle iwrb) {
			return iwrb.getLocalizedString(getRoleKeyForLocalization(StandardRoles.ROLE_KEY_USERADMIN), StandardRoles.ROLE_KEY_USERADMIN);
		}

		@Override
		public String getUri() {
			return new StringBuilder(ADMIN.getUri()).append("/user").toString();
		}
		
	},
	CONTENT_EDITOR {

		@Override
		public String getLocalizedName(IWResourceBundle iwrb) {
			return iwrb.getLocalizedString(getRoleKeyForLocalization(StandardRoles.ROLE_KEY_EDITOR), StandardRoles.ROLE_KEY_EDITOR);
		}

		@Override
		public String getUri() {
			return BUILDER.getUri();
		}
		
	},
	CONTENT_AUTHOR {

		@Override
		public String getLocalizedName(IWResourceBundle iwrb) {
			return iwrb.getLocalizedString(getRoleKeyForLocalization(StandardRoles.ROLE_KEY_AUTHOR), StandardRoles.ROLE_KEY_AUTHOR);
		}

		@Override
		public String getUri() {
			return BUILDER.getUri();
		}
		
	},
	FORM_EDITOR {

		@Override
		public String getLocalizedName(IWResourceBundle iwrb) {
			return iwrb.getLocalizedString(getRoleKeyForLocalization(StandardRoles.ROLE_KEY_FORM_EDITOR), StandardRoles.ROLE_KEY_FORM_EDITOR);
		}

		@Override
		public String getUri() {
			return BUILDER.getUri();
		}
		
	};

	public abstract String getUri();
	public abstract String getLocalizedName(IWResourceBundle iwrb);
	
	public static final String getRoleKeyForLocalization(String roleKey) {
		return new StringBuilder("role_home_page.").append(roleKey).toString();
	}
}

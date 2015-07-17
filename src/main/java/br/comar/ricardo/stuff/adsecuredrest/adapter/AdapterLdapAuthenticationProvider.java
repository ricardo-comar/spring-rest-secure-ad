package br.comar.ricardo.stuff.adsecuredrest.adapter;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.security.ldap.ppolicy.PasswordPolicyException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class AdapterLdapAuthenticationProvider extends
		AbstractLdapAuthenticationProvider {

	private static final Log logger = LogFactory
			.getLog(AdapterLdapAuthenticationProvider.class);
	private LdapAuthoritiesPopulator authoritiesPopulator;
	private ContextSource contextSource;

	public AdapterLdapAuthenticationProvider(ContextSource contextSource,
			LdapAuthoritiesPopulator authoritiesPopulator) {
		this.setAuthoritiesPopulator(authoritiesPopulator);
		this.contextSource = contextSource;
	}

	@Override
	protected DirContextOperations doAuthentication(
			UsernamePasswordAuthenticationToken auth) {
		try {
			return authenticateLDap(auth);
		} catch (PasswordPolicyException ppe) {
			throw new LockedException(messages.getMessage(ppe.getStatus()
					.getErrorCode(), ppe.getStatus().getDefaultMessage()));
		} catch (UsernameNotFoundException notFound) {
			throw new BadCredentialsException(messages.getMessage(
					"LdapAuthenticationProvider.badCredentials",
					"Bad credentials"));
		} catch (NamingException ldapAccessFailure) {
			throw new InternalAuthenticationServiceException(
					ldapAccessFailure.getMessage(), ldapAccessFailure);
		}

	}

	private void setAuthoritiesPopulator(
			LdapAuthoritiesPopulator authoritiesPopulator) {
		Assert.notNull(authoritiesPopulator,
				"An LdapAuthoritiesPopulator must be supplied");
		this.authoritiesPopulator = authoritiesPopulator;
	}

	protected LdapAuthoritiesPopulator getAuthoritiesPopulator() {
		return authoritiesPopulator;
	}

	@Override
	protected Collection<? extends GrantedAuthority> loadUserAuthorities(
			DirContextOperations userData, String username, String password) {
		return getAuthoritiesPopulator().getGrantedAuthorities(userData,
				username);
	}

	public DirContextOperations authenticateLDap(Authentication authentication) {
		DirContextOperations user = null;
		Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class,
				authentication,
				"Can only process UsernamePasswordAuthenticationToken objects");

		String username = authentication.getName();
		String password = (String) authentication.getCredentials();

		if (!StringUtils.hasLength(password)) {
			logger.debug("Rejecting empty password for user " + username);
			throw new BadCredentialsException(messages.getMessage(
					"BindAuthenticator.emptyPassword", "Empty Password"));
		}

		SpringSecurityLdapTemplate ldapTemplate = new SpringSecurityLdapTemplate(
				contextSource);
		try {

			  user = ldapTemplate.searchForSingleEntry("", "(sAMAccountName={0})",
						new String[] { username });

	    } catch (IncorrectResultSizeDataAccessException notFound) {
	    	if (notFound.getActualSize() == 0) {
	    		logger.debug("LdapAuthenticationProvider.User Not Found " + username);
	            throw new UsernameNotFoundException("User " + username + " not found in directory.");
	        }
	        // Search should never return multiple results if properly configured, so just rethrow
	        throw notFound;
	    }	

		if (user == null) {
			logger.debug("LdapAuthenticationProvider.User " + username + " not found in directory.");
			throw new UsernameNotFoundException("User " + username + " not found in directory.");
		}
		EqualsFilter filter = new EqualsFilter("sAMAccountName", username);
		if (!ldapTemplate.authenticate(user.getDn(), filter.toString(),
				password)) {
			logger.debug("Ldap User not Autenticated " + username);
			throw new BadCredentialsException(messages.getMessage(
					"PasswordComparisonAuthenticator.badCredentials",
					"Bad credentials"));
		}
		logger.debug("LdapAuthenticationProvider.User Autenticated " + username);
		return user;
	}
	
}

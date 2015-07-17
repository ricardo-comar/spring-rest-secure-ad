package br.comar.ricardo.stuff.adsecuredrest.mapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

@Component("customUserDetailsContextMapper")
public class UserDetailsContextMapperImpl implements UserDetailsContextMapper,
		Serializable {

	/**	 */
	private static final long serialVersionUID = 6143702853065455574L;
	
	private static final String BASIC_ROLE = "ROLE_USER";

	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx,
			String username, Collection<? extends GrantedAuthority> authority) {

		Set<GrantedAuthority> authorites = new HashSet<GrantedAuthority>();

		if (authority != null && !authority.isEmpty()) {
			authorites.add(new SimpleGrantedAuthority(BASIC_ROLE));
			for (GrantedAuthority authorityAD : authority) {
				authorites.add(new SimpleGrantedAuthority(authorityAD
						.getAuthority()));
			}
		}

		return new User(username, "", authorites);

	}

	@Override
	public void mapUserToContext(UserDetails arg0, DirContextAdapter arg1) {
	}
}

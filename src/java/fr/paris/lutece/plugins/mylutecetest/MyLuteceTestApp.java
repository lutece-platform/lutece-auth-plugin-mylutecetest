/*
 * Copyright (c) 2002-2009, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutecetest;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides information on the current MyLutece Authentication Module
 */
public class MyLuteceTestApp implements XPageApplication
{
    private static final String TEMPLATE_TEST_APP = "/skin/plugins/mylutecetest/mylutecetest.html";
    private static final String BOOKMARK_USER_NAME = "@user_name@";
    private static final String BOOKMARK_GIVEN_NAME = "@user_given_name@";
    private static final String BOOKMARK_FAMILY_NAME = "@user_family_name@";
    private static final String BOOKMARK_USER_ROLES = "@user_roles@";
    private static final String BOOKMARK_AUTHENTICATION_SERVICE = "@authentication_service@";

    /**
     * This method implements the XPageApplication interface
     * @param request The HTTP request
     * @param nMode The current mode
     * @param plugin The plugin which belongs this app
     * @return The XPage object filled depending the request
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws UserNotSignedException
    {
        // TODO : Display the current user infos
        XPage page = new XPage(  );

        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( user == null )
        {
            throw new UserNotSignedException(  );
        }

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_TEST_APP );
        page.setTitle( "MyLuteceTestApp" );
        page.setPathLabel( "MyLuteceTestApp" );

        t.substitute( BOOKMARK_USER_NAME, user.getName(  ) );
        t.substitute( BOOKMARK_GIVEN_NAME, user.getUserInfo( LuteceUser.NAME_GIVEN ) );
        t.substitute( BOOKMARK_FAMILY_NAME, user.getUserInfo( LuteceUser.NAME_FAMILY ) );

        t.substitute( BOOKMARK_USER_ROLES, getRoles( user ) );
        t.substitute( BOOKMARK_AUTHENTICATION_SERVICE, user.getAuthenticationService(  ) );
        page.setContent( t.getHtml(  ) );

        return page;
    }

    /**
     * Gets user roles
     * @param user The Lutece user
     * @return A String containing a comma separated roles list
     */
    private String getRoles( LuteceUser user )
    {
        StringBuffer sbRoles = new StringBuffer(  );
        String[] roles = user.getRoles(  );

        if ( roles != null )
        {
            for ( int i = 0; i < roles.length; i++ )
            {
                if ( i > 0 )
                {
                    sbRoles.append( ", " );
                }

                sbRoles.append( roles[i] );
            }
        }
        else
        {
            sbRoles.append( "No role available" );
        }

        return sbRoles.toString(  );
    }
}

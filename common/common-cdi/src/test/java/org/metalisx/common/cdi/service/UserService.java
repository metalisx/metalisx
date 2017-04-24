package org.metalisx.common.cdi.service;

import javax.ejb.Stateless;

import org.metalisx.common.cdi.interceptor.Log;

@Log("logname")
@Stateless
public class UserService extends AbstractUserService {

}

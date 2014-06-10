package com.flb.entity.support;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.flb.entity.Server;
import com.flb.entity.Server.ServerStatus;
import com.flb.entity.ServerLoad;

@Transactional
public class Repository {
	
	@Resource
	private SessionFactory sessionFactory;
	
	private Integer requestTime;

	public Integer getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Integer requestTime) {
		this.requestTime = requestTime;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	Session getSession(){
		return sessionFactory.getCurrentSession();
	}
	
	public List<ServerLoad> listAllServerLoads(){
		return getSession().createQuery("From "+ServerLoad.class.getName()).list();
	}
	
	public List<Server> listAllServer(){
		return getSession().createQuery("From "+Server.class.getName()).list();
	}
	
	public List<ServerLoad> listMinLoadServers(){
		getSession().flush();
		return getSession().createQuery("From "+ServerLoad.class.getName()+" sl where sl.requestCount=(select min(slinnr.requestCount) FROM "+ServerLoad.class.getName()+" slinnr where slinnr.server.status=:status)")
				.setParameter("status", ServerStatus.ACTIVE).list();
	}
	
	public List<ServerLoad> listMinLoadServersForMigration(Long oldServerId){		
		return getSession().createQuery("From "+ServerLoad.class.getName()+" sl where sl.requestCount=(select min(slinnr.requestCount) FROM "+ServerLoad.class.getName()+" slinnr where slinnr.server.status=:status) AND sl.server.id!=:serverId")
				.setParameter("status", ServerStatus.ACTIVE)
				.setParameter("serverId", oldServerId).list();
	}
	
	public ServerLoad findServerLoadByServer(Server server) {
		return (ServerLoad) getSession()
				.createQuery(
						"FROM " + ServerLoad.class.getName()
								+ " sl where sl.server=:server")
				.setParameter("server", server).uniqueResult();
	}
	
	public Server findServerById(Long id){
		return (Server) getSession().createQuery("From "+Server.class.getName()+" s where s.id=:id")
				.setParameter("id", id).uniqueResult();
	}	

}

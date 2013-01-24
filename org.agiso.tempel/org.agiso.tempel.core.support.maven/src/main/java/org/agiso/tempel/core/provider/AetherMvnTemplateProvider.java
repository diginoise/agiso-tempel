/* org.agiso.tempel.core.provider.AetherMvnTemplateProvider (15-12-2012)
 * 
 * AetherMvnTemplateProvider.java
 * 
 * Copyright 2012 agiso.org
 */
package org.agiso.tempel.core.provider;

import java.io.File;
import java.util.List;

import org.agiso.tempel.Temp;
import org.agiso.tempel.api.model.Template;
import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

/**
 * 
 * 
 * @author <a href="mailto:kkopacz@agiso.org">Karol Kopacz</a>
 */
public class AetherMvnTemplateProvider extends AbstractMvnTemplateProvider {
	private String settingsPath;
	private String repositoryPath;

	private RepositorySystem repoSystem;

	private RemoteRepository local;
	private RemoteRepository central;

//	--------------------------------------------------------------------------
	public AetherMvnTemplateProvider() {
		repoSystem = newRepositorySystem();


		String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		int index = path.lastIndexOf("/repo/");
		// Inicjalizacja repozytoriów z zasobami dla poszczególnych poziomów:
		if(index != -1) {
			// Rzeczywiste środowisko uruchomieniowe (uruchomienie z linni komend):
			settingsPath = System.getProperty("user.home") + "/.m2/repository/";
			repositoryPath = path + System.getProperty("user.dir") + "/.tempel/maven";
		} else {
			// Deweloperskie środowisko uruchomieniowe (uruchomienie z eclipse'a):
			path = System.getProperty("user.dir");					// FIXME: Rodzielić katalogi repozytoriów

			settingsPath = path + "/src/test/resources/repository/maven";
			repositoryPath = path + "/target/local-repo";
		}

		local = new RemoteRepository("local", "default", "file://" + settingsPath);
		central = new RemoteRepository("central", "default", "http://repo1.maven.org/maven2/");
	}

//	--------------------------------------------------------------------------
	/**
	 * @param groupId
	 * @param templateId
	 * @param version
	 * @return
	 */
	@Override
	protected List<File> resolve(String groupId, String templateId, String version) throws Exception {
		Dependency dependency = new Dependency(new DefaultArtifact(groupId + ':' + templateId + ':' + version), null);

		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		collectRequest.addRepository(local);
		collectRequest.addRepository(central);

		RepositorySystemSession session = newSession(repoSystem);

		DependencyNode node = repoSystem.collectDependencies(session, collectRequest).getRoot();
		DependencyRequest dependencyRequest = new DependencyRequest(node, null);

		repoSystem.resolveDependencies(session, dependencyRequest);

		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		node.accept(nlg);
		return nlg.getFiles();
	}

	@Override
	protected String getTemplatePath(Template template) {
		if(Temp.StringUtils_isEmpty(template.getGroupId())) {
			throw new RuntimeException("Szablon MAVEN bez groupId");
		}

		String path = repositoryPath;
		path = path + '/' + template.getGroupId().replace('.', '/');
		path = path + '/' + template.getTemplateId();
		path = path + '/' + template.getVersion();
		path = path + '/' + template.getTemplateId() + '-' +  template.getVersion() + ".jar";
		return path;
	}

//	---------------------------------------------------------------------------
	private static RepositorySystem newRepositorySystem() {
		DefaultServiceLocator locator = new DefaultServiceLocator();
		locator.setServices(WagonProvider.class, new ManualWagonProvider());
		locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);

		return locator.getService(RepositorySystem.class);
	}

	private static RepositorySystemSession newSession(RepositorySystem system) {
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();

		LocalRepository localRepo = new LocalRepository( "target/local-repo" );
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));

		return session;
	}

//	--------------------------------------------------------------------------
	static class ManualWagonProvider implements WagonProvider {
		@Override
		public Wagon lookup(final String roleHint) throws Exception {
			if("file".equals(roleHint)) {
				return new FileWagon();
			} else if("http".equals(roleHint)) {
				return new LightweightHttpWagon();
			}
//			else if(roleHint != null && roleHint.startsWith("http")) { // http and https
//				return new HttpWagon();
//			}
			return null;
		}
		@Override
		public void release(final Wagon wagon) {
			// intentionally empty
		}
	}
}

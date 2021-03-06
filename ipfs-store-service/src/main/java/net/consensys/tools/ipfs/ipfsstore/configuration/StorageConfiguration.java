package net.consensys.tools.ipfs.ipfsstore.configuration;

import static net.consensys.tools.ipfs.ipfsstore.Constant.ERROR_NOT_NULL_OR_EMPTY;
import static net.consensys.tools.ipfs.ipfsstore.Constant.STORAGE_IPFS;
import static net.consensys.tools.ipfs.ipfsstore.Constant.STORAGE_SWARM;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;

import io.ipfs.api.IPFS;
import lombok.extern.slf4j.Slf4j;
import net.consensys.tools.ipfs.ipfsstore.configuration.health.HealthCheckScheduler;
import net.consensys.tools.ipfs.ipfsstore.dao.StorageDao;
import net.consensys.tools.ipfs.ipfsstore.dao.storage.IPFSStorageDao;
import net.consensys.tools.ipfs.ipfsstore.exception.ConnectionException;

/**
 * Configuration for the storage layer
 *
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ipfs-store.storage")
@DependsOn("HealthCheckScheduler")
@Slf4j
public class StorageConfiguration extends AbstractConfiguration {

  private HealthCheckScheduler healthCheckScheduler;

  @Autowired
  public StorageConfiguration(HealthCheckScheduler healthCheckScheduler) {
    this.healthCheckScheduler = healthCheckScheduler;
  }
  
  /**
   * Load the IPFS Storage Dao bean up
   * 
   * @return IPFS StorageDAO bean
   */
  @Bean
  @ConditionalOnProperty(value = "ipfs-store.storage.type", havingValue = STORAGE_IPFS)
  public StorageDao ipfsStorageDao() throws ConnectionException {

    if (StringUtils.isEmpty(host))
      throw new IllegalArgumentException("ipfs-store.storage.host" + ERROR_NOT_NULL_OR_EMPTY);
    if (StringUtils.isEmpty(port))
      throw new IllegalArgumentException("ipfs-store.storage.host" + ERROR_NOT_NULL_OR_EMPTY);

    try {
      log.info("Connecting to IPFS [host: {}, ipfsPort: {}]", host, port);

      IPFS ipfs = new IPFS(host, port);

      StorageDao bean = new IPFSStorageDao(ipfs);
      
      // Register to the heath check service
      healthCheckScheduler.registerHealthCheck("ipfs", bean);
      
      log.info("Connected to IPFS [host: {}, ipfsPort: {}]", host, port);
      log.debug(ipfs.config.show().toString());

      return bean;

    } catch (IOException ex) {
      log.error("Error while connecting to IPFS [host: {}, ipfsPort: {}", host, port);
      throw new ConnectionException("Error while connecting to IPFS", ex);
    }
  }

  /**
   * Load the Swarm Storage Dao bean up
   * 
   * @return Swarm StorageDAO bean
   */
  @Bean
  @ConditionalOnProperty(value = "ipfs-store.storage.type", havingValue = STORAGE_SWARM)
  public StorageDao swarmStorageDao() throws ConnectionException {
    throw new UnsupportedOperationException("Swarm StorageDAO is not implemented yet !");
  }

}

package cc.coopersoft.keycloak.phone.providers.sender;

import cc.coopersoft.keycloak.phone.providers.exception.MessageSendException;
import cc.coopersoft.keycloak.phone.providers.spi.FullSmsSenderAbstractService;
import org.jboss.logging.Logger;

import java.util.Random;

public class DummySmsSenderService extends FullSmsSenderAbstractService {

    private static final Logger logger = Logger.getLogger(DummySmsSenderService.class);

    public DummySmsSenderService(String realmDisplay) {
        super(realmDisplay);
    }

    @Override
    public void sendMessage(String phoneNumber, String message) throws MessageSendException {
        logger.info(String.format("To: %s >>> %s", phoneNumber, message));
    }

    @Override
    public void close() {
    }
}

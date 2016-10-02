package pl.pamsoft.imapcloud.dao;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.pamsoft.imapcloud.config.GraphProperties;
import pl.pamsoft.imapcloud.config.ODB;
import pl.pamsoft.imapcloud.entity.Account;
import pl.pamsoft.imapcloud.entity.File;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.Iterator;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_ABSOLUTE_PATH;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_COMPLETED;
import static pl.pamsoft.imapcloud.config.GraphProperties.FILE_HASH;

public class FileRepositoryTest {

	private FileRepository fileRepository = new FileRepository();

	private ODB db = mock(ODB.class);
	private OrientGraphNoTx graphNoTx = mock(OrientGraphNoTx.class);

	private Iterable<Vertex> result = mock(Iterable.class);
	private Iterator<Vertex> mockedIterator = mock(Iterator.class);

	private File exampleExistingFile;
	private OrientVertex createdVertex = mock(OrientVertex.class);
	private String[] keys;
	private Object[] values;

	@BeforeMethod
	@SuppressWarnings("unchecked")
	public void setup() {
		String ownerId = randomAlphanumeric(2);
		Account owner = new Account();
		owner.setId(ownerId);
		exampleExistingFile = new File();
		exampleExistingFile.setFileHash(randomAlphanumeric(16));
		exampleExistingFile.setAbsolutePath(Paths.get(randomAlphanumeric(3), randomAlphanumeric(5)).toString());
		exampleExistingFile.setOwnerAccount(owner);
		exampleExistingFile.setCompleted(true);

		keys = new String[] {FILE_ABSOLUTE_PATH, FILE_HASH};
		values = new Object[] {exampleExistingFile.getAbsolutePath(), exampleExistingFile.getFileHash()};

		reset(db, graphNoTx, result, mockedIterator, createdVertex);
		when(db.getGraphDB()).thenReturn(graphNoTx);
		when(result.iterator()).thenReturn(mockedIterator);
		when(graphNoTx.getVertices(File.class.getSimpleName(), keys, values)).thenReturn(result);
		when(graphNoTx.addVertex("class:" + File.class.getSimpleName(),
			FILE_ABSOLUTE_PATH, exampleExistingFile.getAbsolutePath(),
			FILE_HASH, exampleExistingFile.getFileHash(),
			FILE_COMPLETED, exampleExistingFile.isCompleted())).thenReturn(createdVertex);
		when(createdVertex.getId()).thenReturn(new ORecordId(nextInt(32767), nextInt(32767)));
		ODocument document = mock(ODocument.class);
		when(document.getVersion()).thenReturn(nextInt());
		when(createdVertex.getRecord()).thenReturn(document);

		fileRepository.setDb(db);
	}

	@Test
	public void shouldSaveFile() throws FileAlreadyExistsException {
		//given
		when(mockedIterator.hasNext()).thenReturn(Boolean.FALSE);

		//when
		File saved = fileRepository.save(exampleExistingFile);

		assertEquals(saved.getName(), exampleExistingFile.getName());
		assertEquals(saved.getAbsolutePath(), exampleExistingFile.getAbsolutePath());
	}

	@Test(expectedExceptions = FileAlreadyExistsException.class)
	public void shouldThrowExceptionWhenFileAlreadyExists() throws FileAlreadyExistsException {
		//given
		when(mockedIterator.hasNext()).thenReturn(Boolean.TRUE);

		//when
		fileRepository.save(exampleExistingFile);
	}
}

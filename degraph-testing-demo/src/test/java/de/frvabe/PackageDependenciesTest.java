package de.frvabe;

import static de.schauderhaft.degraph.check.Check.classpath;
import static de.schauderhaft.degraph.check.JCheck.violationFree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class PackageDependenciesTest {

    @Test(expected = AssertionError.class)
    public void allPackagesCheck() {
        assertThat(classpath().including("de.frvabe.**"), is(violationFree()));
    }

    @Test(expected = AssertionError.class)
    public void cycleFreePackageDepedencies() {
        assertThat(
                classpath().including("de.frvabe.**").withSlicing("a", "de.frvabe.a.*")
                        .withSlicing("b", "de.frvabe.b.*").withSlicing("bc", "de.frvabe.b.c.*"),
                is(violationFree()));
    }

}

<img src="http://amadeus.social/o/images/amadeus-screen.png" width=450/>

# Amadeus

interstellar
astral, star, starry, stellar
celestial, empyrean, heavenly
astronomical (also astronomic), astrophysical
astronautic (or astronautical)
starlike, star-spangled


Mkio
BQi

<bean depends-on="dataSource" class="org.springframework.beans.factory.config.MethodInvokingBean">
    <property name="targetClass" value="org.hsqldb.util.DatabaseManagerSwing" />
    <property name="targetMethod" value="main" />
    <property name="arguments">
        <list>
            <value>--url</value>
            <!-- <value>jdbc:hsqldb:memory:oa</value> -->
            <value>jdbc:h2:mem:oa</value>
            <value>--user</value>
            <value>sa</value>
            <value>--password</value>
            <value></value>
        </list>
    </property>
</bean>

mvn install:install-file -Dfile=output/parakeet-0.5-SNAPSHOT.jar -DgroupId=io.github.mcroteau -DartifactId=parakeet -Dversion=0.5-SNAPSHOT -Dpackaging=jar

GASTC775
Q84RSG8Q
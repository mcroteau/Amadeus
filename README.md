<img src="http://amadeus.social/o/images/amadeus-screen.png" width=450/>

# Amadeus

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
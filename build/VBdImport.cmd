set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_40
set JVM_OPS=-Xmx800m -Dstorage.diskCache.bufferSize=3000 -Dstorage.useWAL=false -Dstorage.wal.syncOnPageFlush=false
"%JAVA_HOME%\bin\java" %JVM_OPS% -classpath ./* com.siams.orientdb.evaluation.VDbImport %1 %2

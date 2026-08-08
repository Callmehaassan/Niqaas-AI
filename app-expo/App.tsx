import React, { useState } from 'react';
import { StyleSheet, View, Alert, SafeAreaView } from 'react-native';
import Login from './src/screens/Login';
import CitizenReport from './src/screens/CitizenReport';
import AuthorityDashboard from './src/screens/AuthorityDashboard';
import IncidentDetail from './src/screens/IncidentDetail';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<'Login' | 'CitizenReport' | 'AuthorityDashboard' | 'IncidentDetail'>('Login');

  const handleRoleContinue = (role: 'Citizen' | 'Authority') => {
    if (role === 'Citizen') {
      setCurrentScreen('CitizenReport');
    } else {
      setCurrentScreen('AuthorityDashboard');
    }
  };

  const handleReportSubmit = () => {
    Alert.alert(
      'Report Submitted',
      'Your flood report has been submitted successfully and is being fused with weather and traffic feeds.',
      [{ text: 'OK', onPress: () => setCurrentScreen('Login') }]
    );
  };

  const handleSimulateResponse = () => {
    Alert.alert(
      'Simulation Initiated',
      'Emergency response simulated! Rerouting traffic, dispatching teams, and resident warnings active.',
      [{ text: 'View Incident Detail', onPress: () => setCurrentScreen('IncidentDetail') }]
    );
  };

  const renderScreen = () => {
    switch (currentScreen) {
      case 'Login':
        return <Login onContinue={handleRoleContinue} />;
      case 'CitizenReport':
        return (
          <CitizenReport
            onBack={() => setCurrentScreen('Login')}
            onSubmit={handleReportSubmit}
          />
        );
      case 'AuthorityDashboard':
        return (
          <AuthorityDashboard
            onBack={() => setCurrentScreen('Login')}
            onSelectIncident={(name) => {
              if (name === 'G-10 Underpass') {
                setCurrentScreen('IncidentDetail');
              } else {
                Alert.alert('Info', `${name} details simulation is coming soon.`);
              }
            }}
            onSimulateFullResponse={handleSimulateResponse}
          />
        );
      case 'IncidentDetail':
        return <IncidentDetail onBack={() => setCurrentScreen('AuthorityDashboard')} />;
      default:
        return <Login onContinue={handleRoleContinue} />;
    }
  };

  return (
    <View style={styles.container}>
      {renderScreen()}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0B1F3A',
  },
});


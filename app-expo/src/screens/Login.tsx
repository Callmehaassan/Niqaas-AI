import React, { useState } from 'react';
import { StyleSheet, Text, View, Image, TouchableOpacity, Dimensions } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

interface LoginProps {
  onContinue: (role: 'Citizen' | 'Authority') => void;
}

export default function Login({ onContinue }: LoginProps) {
  const [selectedRole, setSelectedRole] = useState<'Citizen' | 'Authority'>('Citizen');

  return (
    <View style={styles.container}>
      {/* Top 40% */}
      <View style={styles.topContainer}>
        <Image
          source={require('../../assets/logo.jpg')}
          style={styles.logoImage}
          resizeMode="cover"
        />
        <View style={styles.titleContainer}>
          <Text style={styles.titleBold}>Nikaas</Text>
          <Text style={styles.titleTeal}>نکاس</Text>
        </View>
        <Text style={styles.tagline}>Flood response. Before it's too late.</Text>
      </View>

      {/* Middle */}
      <View style={styles.middleContainer}>
        <View style={styles.roleCardRow}>
          <TouchableOpacity
            style={[
              styles.roleCard,
              selectedRole === 'Citizen' ? styles.roleCardSelected : styles.roleCardUnselected
            ]}
            onPress={() => setSelectedRole('Citizen')}
            activeOpacity={0.8}
          >
            <Ionicons
              name="person"
              size={36}
              color={selectedRole === 'Citizen' ? '#00C2B2' : '#8899AA'}
            />
            <Text style={[styles.roleText, selectedRole === 'Citizen' && styles.roleTextSelected]}>
              Citizen
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.roleCard,
              selectedRole === 'Authority' ? styles.roleCardSelected : styles.roleCardUnselected
            ]}
            onPress={() => setSelectedRole('Authority')}
            activeOpacity={0.8}
          >
            <Ionicons
              name="shield"
              size={36}
              color={selectedRole === 'Authority' ? '#00C2B2' : '#8899AA'}
            />
            <Text style={[styles.roleText, selectedRole === 'Authority' && styles.roleTextSelected]}>
              Authority
            </Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Bottom */}
      <View style={styles.bottomContainer}>
        <TouchableOpacity
          style={styles.continueButton}
          onPress={() => onContinue(selectedRole)}
          activeOpacity={0.9}
        >
          <Text style={styles.continueButtonText}>Continue</Text>
        </TouchableOpacity>
        <Text style={styles.powerByText}>Powered by Gemini AI</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0B1F3A',
    justifyContent: 'space-between',
    paddingBottom: 24,
  },
  topContainer: {
    height: '40%',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 40,
    paddingHorizontal: 16,
  },
  logoImage: {
    width: 120,
    height: 120,
    borderRadius: 60,
    borderWidth: 2,
    borderColor: '#00C2B2',
    marginBottom: 16,
  },
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  titleBold: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginRight: 8,
  },
  titleTeal: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#00C2B2',
  },
  tagline: {
    fontSize: 14,
    color: '#8899AA',
    marginTop: 8,
    textAlign: 'center',
  },
  middleContainer: {
    justifyContent: 'center',
    marginVertical: 20,
    paddingHorizontal: 16,
  },
  roleCardRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  roleCard: {
    width: '48%',
    height: 140,
    backgroundColor: '#1E3248',
    borderRadius: 12,
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
  },
  roleCardUnselected: {
    borderColor: '#2A4060',
  },
  roleCardSelected: {
    borderColor: '#00C2B2',
  },
  roleText: {
    fontSize: 16,
    color: '#8899AA',
    marginTop: 12,
    fontWeight: '600',
  },
  roleTextSelected: {
    color: '#FFFFFF',
  },
  bottomContainer: {
    width: '100%',
    alignItems: 'center',
  },
  continueButton: {
    width: '100%',
    height: 56,
    backgroundColor: '#00C2B2',
    justifyContent: 'center',
    alignItems: 'center',
  },
  continueButtonText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  powerByText: {
    fontSize: 11,
    color: '#8899AA',
    marginTop: 12,
  },
});

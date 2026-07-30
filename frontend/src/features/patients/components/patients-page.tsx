import { useState, useEffect, useCallback } from 'react';
import { PatientModal } from './modals/patient-modal';
import type { Patient } from 'src/shared/model';
import { DataTable } from 'src/shared/ui/data-table';
import { Card, CardHeader, CardTitle, CardContent } from 'src/shared/ui/card';
import { Users, UserCheck, UserPlus } from 'lucide-react';

export const PatientsPage = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [patients, setPatients] = useState<Patient[]>([]);
    const [filteredPatients, setFilteredPatients] = useState<Patient[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [currentPage] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const apiBase = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

    const fetchPatients = useCallback(async () => {
        setIsLoading(true);
        try {
            const response = await fetch(`${apiBase}/users/patients?page=${currentPage}&size=5`);
            if (!response.ok) throw new Error('Failed to fetch patients');
            const data = await response.json();

            const content = data.content || [];
            const mapped: Patient[] = content.map((user: {
                id: string | number;
                firstName: string;
                lastName: string;
                email: string;
                phone?: string;
                updatedAt?: string;
                createdAt?: string;
            }) => ({
                id: Number(user.id) || 0,
                name: `${user.firstName} ${user.lastName}`,
                email: user.email || '',
                phone: user.phone || 'Non renseigné',
                lastVisit: user.updatedAt || user.createdAt || new Date().toISOString()
            }));

            setPatients(mapped);
            setFilteredPatients(mapped);
            setTotalElements(data.totalElements || 0);
        } catch (error) {
            console.error('Error fetching patients:', error);
        } finally {
            setIsLoading(false);
        }
    }, [apiBase, currentPage]);

    useEffect(() => {
        fetchPatients();
    }, [fetchPatients]);

    const formatDate = (dateStr: string) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    const totalPatientsCount = totalElements;
    const activePatientsCount = patients.length;
    const newThisMonthCount = patients.filter(p => {
        const d = new Date(p.lastVisit);
        const now = new Date();
        return d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear();
    }).length;

    return (
        <div>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5 mb-8">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium">Total Patients</CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{totalPatientsCount}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium">Patients récents</CardTitle>
                        <UserCheck className="h-4 w-4 text-[var(--accent)]" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-[var(--accent)]">{activePatientsCount}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium">Nouveaux ce mois</CardTitle>
                        <UserPlus className="h-4 w-4 text-green-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-green-500">{newThisMonthCount}</div>
                    </CardContent>
                </Card>
            </div>

            <DataTable<Patient>
                columns={[
                    { id: 'id', header: 'ID', cell: (row) => <span className="font-medium text-muted-foreground">#{String(row.id).substring(0, 8)}</span> },
                    { id: 'name', header: 'Nom complet', cell: (row) => <span className="font-semibold">{row.name}</span> },
                    { id: 'email', header: 'Email', cell: (row) => row.email },
                    { id: 'phone', header: 'Téléphone', cell: (row) => row.phone },
                    { id: 'lastVisit', header: 'Dernière visite', cell: (row) => formatDate(row.lastVisit) },
                ]}
                data={filteredPatients}
                getRowId={(row) => String(row.id)}
                isLoading={isLoading}
                emptyMessage="Aucun patient trouvé"
            />

            <PatientModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />
        </div>
    );
};